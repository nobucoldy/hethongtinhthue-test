// Các hàm tiện ích dùng chung
const chartInstances = {};

function formatVnd(amount) {
    if (isNaN(amount) || amount === null || amount === undefined) return '0 đ';
    return new Intl.NumberFormat('vi-VN').format(Math.round(amount)) + ' đ';
}

function showToast(message, isSuccess = true) {
    alert((isSuccess ? '✓ ' : '✗ ') + message);
}

// Kiểm tra phiên đăng nhập
async function checkAuth() {
    try {
        const res = await fetch('/api/auth/me');
        if (res.ok) {
            const data = await res.json();
            return data.user;
        }
    } catch (e) {
        console.error('Lỗi kiểm tra auth:', e);
    }
    return null;
}

// Đăng xuất
async function logout() {
    try {
        await fetch('/api/auth/logout', { method: 'POST' });
        window.location.href = '/index.html';
    } catch (e) {
        window.location.href = '/index.html';
    }
}

// Tính thuế nhanh (trên index hoặc dashboard)
async function performQuickCalculate(grossSalary, bonusIncome, otherIncome, nonTaxableIncome, dependentCount, charityDonation) {
    const payload = {
        grossSalary: parseFloat(grossSalary) || 0,
        bonusIncome: parseFloat(bonusIncome) || 0,
        otherIncome: parseFloat(otherIncome) || 0,
        nonTaxableIncome: parseFloat(nonTaxableIncome) || 0,
        dependentCount: parseInt(dependentCount) || 0,
        charityDonation: parseFloat(charityDonation) || 0
    };

    const res = await fetch('/api/tax/quick-calculate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    const data = await res.json();
    if (!res.ok || !data.success) {
        throw new Error(data.message || 'Lỗi tính thuế');
    }
    return data.data;
}

// Vẽ biểu đồ tròn cơ cấu thu nhập (Chart.js)
function updateIncomeDoughnutChart(canvasId, data, pctPrefix) {
    const total = Math.max(1, data.totalGrossIncome || 0);
    const net = Math.max(0, data.netIncome || 0);
    const ins = Math.max(0, data.totalInsurance || 0);
    const tax = Math.max(0, data.totalTax || 0);

    const netPct = ((net / total) * 100).toFixed(1) + '%';
    const insPct = ((ins / total) * 100).toFixed(1) + '%';
    const taxPct = ((tax / total) * 100).toFixed(1) + '%';

    const elNet = document.getElementById(pctPrefix + 'NetPct');
    const elIns = document.getElementById(pctPrefix + 'InsPct');
    const elTax = document.getElementById(pctPrefix + 'TaxPct');
    if (elNet) elNet.innerText = netPct;
    if (elIns) elIns.innerText = insPct;
    if (elTax) elTax.innerText = taxPct;

    const canvas = document.getElementById(canvasId);
    if (!canvas || typeof Chart === 'undefined') return;

    const chartValues = (data.totalGrossIncome > 0) ? [net, ins, tax] : [1, 0, 0];

    if (chartInstances[canvasId]) {
        chartInstances[canvasId].data.datasets[0].data = chartValues;
        chartInstances[canvasId].update();
    } else {
        chartInstances[canvasId] = new Chart(canvas, {
            type: 'doughnut',
            data: {
                labels: ['Thực nhận (Net)', 'Bảo hiểm (10.5%)', 'Thuế TNCN'],
                datasets: [{
                    data: chartValues,
                    backgroundColor: ['#10b981', '#0ea5e9', '#f59e0b'],
                    borderWidth: 2,
                    borderColor: '#ffffff',
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '68%',
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        callbacks: {
                            label: function(ctx) {
                                return ' ' + ctx.label + ': ' + formatVnd(ctx.raw);
                            }
                        }
                    }
                }
            }
        });
    }
}

// Hiển thị bảng 5 bậc thuế kèm thanh tiến trình trực quan (Progress Bar)
function renderBracketTableWithProgress(tbodyId, bracketDetails) {
    const tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    tbody.innerHTML = '';

    // Độ rộng tối đa của mỗi bậc (10tr, 20tr, 30tr, 40tr, 50tr+)
    const bracketMaxCaps = [10000000, 20000000, 30000000, 40000000, 60000000];
    const barColors = ['#10b981', '#0ea5e9', '#6366f1', '#f59e0b', '#dc2626'];

    if (bracketDetails && bracketDetails.length > 0) {
        bracketDetails.forEach((b, idx) => {
            const tr = document.createElement('tr');
            const isActive = b.taxAmount > 0;
            if (isActive) tr.classList.add('bracket-active-row');

            const cap = bracketMaxCaps[idx] || 50000000;
            const fillPct = isActive ? Math.min(100, Math.max(6, Math.round((b.incomeInBracket / cap) * 100))) : 0;
            const color = barColors[idx] || '#15437d';

            tr.innerHTML = `
                <td class="text-nowrap">
                    <span class="badge" style="background-color: ${isActive ? color : '#94a3b8'};">Bậc ${b.bracket}</span>
                </td>
                <td class="text-start">
                    <div class="d-flex justify-content-between small gap-2">
                        <span class="text-muted text-nowrap">${b.description || ''}</span>
                        <strong class="text-nowrap ${isActive ? 'text-dark' : 'text-muted'}">${formatVnd(b.incomeInBracket)}</strong>
                    </div>
                    <div class="bracket-progress-wrap">
                        <div class="bracket-progress-bar" style="width: ${fillPct}%; background-color: ${color};"></div>
                    </div>
                </td>
                <td class="text-nowrap"><span class="fw-semibold">${(b.rate * 100).toFixed(0)}%</span></td>
                <td class="fw-bold text-end text-nowrap ${isActive ? 'text-danger' : 'text-muted'}">${formatVnd(b.taxAmount)}</td>
            `;
            tbody.appendChild(tr);
        });
    }
}
