// Các hàm tiện ích dùng chung
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
