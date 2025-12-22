const API_BASE = '/api';

// 检查登录状态
async function checkLogin() {
    try {
        const response = await fetch(`${API_BASE}/auth/current`);
        const result = await response.json();
        if (!result.success) {
            window.location.href = '/index.html';
            return;
        }
        document.getElementById('userName').textContent = result.data.name || result.data.username;
    } catch (error) {
        window.location.href = '/index.html';
    }
}

// 退出登录
document.getElementById('logoutBtn').addEventListener('click', async () => {
    await fetch(`${API_BASE}/auth/logout`, { method: 'POST' });
    window.location.href = '/index.html';
});

// 标签页切换
document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
        btn.classList.add('active');
        const tab = btn.getAttribute('data-tab');
        document.getElementById(tab === 'query' ? 'queryTab' : 'myBookingsTab').classList.add('active');
        
        if (tab === 'my-bookings') {
            loadMyBookings();
        }
    });
});

// 查询会议室
document.getElementById('queryForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = {
        date: document.getElementById('date').value,
        startTime: document.getElementById('startTime').value,
        endTime: document.getElementById('endTime').value,
        location: document.getElementById('location').value || null,
        minCapacity: document.getElementById('minCapacity').value ? parseInt(document.getElementById('minCapacity').value) : null,
        equipment: null
    };
    
    try {
        const response = await fetch(`${API_BASE}/rooms/query`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.success) {
            displayRooms(result.data);
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('查询失败，请稍后重试');
    }
});

// 显示会议室列表
function displayRooms(rooms) {
    const container = document.getElementById('roomList');
    
    if (rooms.length === 0) {
        container.innerHTML = '<div class="room-card"><p>未找到符合条件的可用会议室</p></div>';
        return;
    }
    
    container.innerHTML = rooms.map(room => `
        <div class="room-card">
            <h3>${room.name}</h3>
            <div class="info">
                <span>📍 ${room.location}</span>
                <span>👥 可容纳 ${room.capacity} 人</span>
                <span class="status">可用</span>
            </div>
            <div class="equipment">
                ${room.equipment && room.equipment.length > 0 
                    ? room.equipment.map(eq => `<span>${eq}</span>`).join('')
                    : '<span>无特殊设备</span>'}
            </div>
            <button class="btn btn-primary" onclick="openBookingModal(${room.id}, '${room.name}', '${document.getElementById('date').value}', '${document.getElementById('startTime').value}', '${document.getElementById('endTime').value}')">
                预约
            </button>
        </div>
    `).join('');
}

// 打开预约弹窗
function openBookingModal(roomId, roomName, date, startTime, endTime) {
    document.getElementById('bookingRoomId').value = roomId;
    document.getElementById('bookingRoomName').textContent = roomName;
    document.getElementById('bookingDate').textContent = date;
    document.getElementById('bookingTime').textContent = `${startTime} - ${endTime}`;
    document.getElementById('bookingModal').style.display = 'block';
}

// 关闭预约弹窗
document.querySelector('.close').addEventListener('click', () => {
    document.getElementById('bookingModal').style.display = 'none';
});

document.getElementById('cancelBookingBtn').addEventListener('click', () => {
    document.getElementById('bookingModal').style.display = 'none';
});

// 创建预约
document.getElementById('bookingForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const formData = {
        roomId: parseInt(document.getElementById('bookingRoomId').value),
        date: document.getElementById('bookingDate').textContent,
        startTime: document.getElementById('bookingTime').textContent.split(' - ')[0],
        endTime: document.getElementById('bookingTime').textContent.split(' - ')[1],
        subject: document.getElementById('subject').value,
        attendeeCount: document.getElementById('attendeeCount').value ? parseInt(document.getElementById('attendeeCount').value) : null,
        remark: document.getElementById('remark').value || null
    };
    
    try {
        const response = await fetch(`${API_BASE}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(formData)
        });
        
        const result = await response.json();
        
        if (result.success) {
            showSuccess('预约成功！');
            document.getElementById('bookingModal').style.display = 'none';
            document.getElementById('bookingForm').reset();
            // 刷新查询结果
            document.getElementById('queryForm').dispatchEvent(new Event('submit'));
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('预约失败，请稍后重试');
    }
});

// 加载我的预约
async function loadMyBookings() {
    const startDate = document.getElementById('startDate').value || null;
    const endDate = document.getElementById('endDate').value || null;
    const status = document.getElementById('statusFilter').value || null;
    
    let url = `${API_BASE}/bookings/my`;
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    if (status) params.append('status', status);
    if (params.toString()) url += '?' + params.toString();
    
    try {
        const response = await fetch(url);
        const result = await response.json();
        
        if (result.success) {
            displayBookings(result.data);
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('加载失败，请稍后重试');
    }
}

// 显示预约列表
function displayBookings(bookings) {
    const container = document.getElementById('bookingList');
    
    if (bookings.length === 0) {
        container.innerHTML = '<div class="booking-card"><p>暂无预约记录</p></div>';
        return;
    }
    
    container.innerHTML = bookings.map(booking => {
        const statusText = {
            'PENDING': '待开始',
            'CHECKED_IN': '已签到',
            'COMPLETED': '已结束',
            'CANCELLED': '已取消'
        };
        
        let actions = '';
        if (booking.status === 'PENDING') {
            actions = `
                <button class="btn btn-success" onclick="checkIn(${booking.id})">签到</button>
                <button class="btn btn-danger" onclick="cancelBooking(${booking.id})">取消</button>
            `;
        }
        
        return `
            <div class="booking-card">
                <div class="info">
                    <h3>${booking.subject}</h3>
                    <div class="detail">会议室：${booking.roomName}</div>
                    <div class="detail">时间：${booking.date} ${booking.startTime} - ${booking.endTime}</div>
                    <div class="detail">状态：<span class="status ${booking.status}">${statusText[booking.status]}</span></div>
                    ${booking.attendeeCount ? `<div class="detail">参会人数：${booking.attendeeCount}</div>` : ''}
                    ${booking.remark ? `<div class="detail">备注：${booking.remark}</div>` : ''}
                </div>
                <div class="actions">
                    ${actions}
                </div>
            </div>
        `;
    }).join('');
}

// 取消预约
async function cancelBooking(bookingId) {
    if (!confirm('确定要取消这个预约吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE}/bookings/${bookingId}/cancel`, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showSuccess('取消成功！');
            loadMyBookings();
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('取消失败，请稍后重试');
    }
}

// 签到
async function checkIn(bookingId) {
    try {
        const response = await fetch(`${API_BASE}/bookings/${bookingId}/checkin`, {
            method: 'POST'
        });
        
        const result = await response.json();
        
        if (result.success) {
            showSuccess('签到成功！');
            loadMyBookings();
        } else {
            showError(result.message);
        }
    } catch (error) {
        showError('签到失败，请稍后重试');
    }
}

// 筛选表单提交
document.getElementById('filterForm').addEventListener('submit', (e) => {
    e.preventDefault();
    loadMyBookings();
});

// 显示错误消息
function showError(message) {
    const errorDiv = document.getElementById('errorMessage');
    errorDiv.textContent = message;
    errorDiv.classList.add('show');
    setTimeout(() => {
        errorDiv.classList.remove('show');
    }, 3000);
}

// 显示成功消息
function showSuccess(message) {
    const successDiv = document.createElement('div');
    successDiv.className = 'success-message show';
    successDiv.textContent = message;
    document.body.appendChild(successDiv);
    setTimeout(() => {
        successDiv.remove();
    }, 3000);
}

// 初始化
checkLogin();
// 设置默认日期为今天
document.getElementById('date').valueAsDate = new Date();

