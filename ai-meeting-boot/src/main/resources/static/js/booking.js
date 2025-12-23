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
    
    // 清空表单
    document.getElementById('subject').value = '';
    document.getElementById('attendeeCount').value = '';
    document.getElementById('remark').value = '';
    
    // 显示模态框
    document.getElementById('bookingModal').style.display = 'block';
    
    // 等待DOM更新后设置按钮显示逻辑
    setTimeout(() => {
        setupAgendaButtonDisplay();
    }, 10);
    
    // 确保事件监听器已绑定（使用事件委托，只需绑定一次）
    setupAgendaGenerationHandler();
}

// 设置AI生成议程按钮的显示逻辑
function setupAgendaButtonDisplay() {
    const subjectInput = document.getElementById('subject');
    const generateBtn = document.getElementById('generateAgendaBtn');
    
    if (!subjectInput) {
        console.error('未找到subject输入框');
        return;
    }
    
    if (!generateBtn) {
        console.error('未找到generateAgendaBtn按钮');
        return;
    }
    
    // 先隐藏按钮（使用setProperty确保可以覆盖内联样式）
    generateBtn.style.setProperty('display', 'none', 'important');
    generateBtn.style.setProperty('visibility', 'visible', 'important');
    
    // 定义处理函数（每次重新获取元素引用，确保正确）
    const handleSubjectInput = function(event) {
        const currentInput = document.getElementById('subject');
        const currentBtn = document.getElementById('generateAgendaBtn');
        
        if (!currentInput || !currentBtn) {
            console.warn('handleSubjectInput: 元素不存在', { currentInput: !!currentInput, currentBtn: !!currentBtn });
            return;
        }
        
        const inputValue = currentInput.value || '';
        const hasContent = inputValue.trim().length > 0;
        
        console.log('handleSubjectInput触发:', { 
            value: inputValue, 
            hasContent: hasContent,
            eventType: event ? event.type : 'manual'
        });
        
        if (hasContent) {
            // 使用setProperty确保可以覆盖任何样式（包括!important）
            currentBtn.style.setProperty('display', 'block', 'important');
            currentBtn.style.setProperty('visibility', 'visible', 'important');
            console.log('按钮已显示', { 
                computedDisplay: window.getComputedStyle(currentBtn).display,
                inlineDisplay: currentBtn.style.display 
            });
        } else {
            currentBtn.style.setProperty('display', 'none', 'important');
            console.log('按钮已隐藏');
        }
    };
    
    // 移除旧的事件监听器（通过克隆节点）
    const newSubjectInput = subjectInput.cloneNode(true);
    subjectInput.parentNode.replaceChild(newSubjectInput, subjectInput);
    
    // 重新获取引用
    const updatedSubjectInput = document.getElementById('subject');
    if (!updatedSubjectInput) {
        console.error('替换输入框后无法重新获取元素');
        return;
    }
    
    // 绑定多个事件，确保实时响应
    updatedSubjectInput.addEventListener('input', handleSubjectInput, { passive: true });
    updatedSubjectInput.addEventListener('keyup', handleSubjectInput, { passive: true });
    updatedSubjectInput.addEventListener('change', handleSubjectInput, { passive: true });
    updatedSubjectInput.addEventListener('paste', function(e) {
        // 粘贴事件后延迟检查，确保内容已更新
        setTimeout(() => handleSubjectInput(e), 10);
    }, { passive: true });
    
    // 立即检查一次（处理已有内容的情况，比如用户粘贴或程序设置值）
    setTimeout(() => {
        handleSubjectInput({ type: 'manual' });
    }, 50);
    
    console.log('setupAgendaButtonDisplay: 按钮显示逻辑已设置');
}

// 关闭预约弹窗
document.querySelector('.close').addEventListener('click', () => {
    document.getElementById('bookingModal').style.display = 'none';
});

document.getElementById('cancelBookingBtn').addEventListener('click', () => {
    document.getElementById('bookingModal').style.display = 'none';
});

// AI生成议程 - 使用事件委托，在document上绑定事件，确保即使按钮被替换也能正常工作
let agendaGenerationHandlerBound = false;

function setupAgendaGenerationHandler() {
    // 如果已经绑定过，不再重复绑定（使用事件委托，只需绑定一次）
    if (agendaGenerationHandlerBound) {
        console.log('事件监听器已绑定，跳过重复绑定');
        return;
    }
    
    // 使用事件委托，在document上绑定click事件，这样可以捕获所有按钮点击
    document.addEventListener('click', async (event) => {
        // 检查点击的是否是AI生成议程按钮（通过ID或类名判断）
        const clickedElement = event.target;
        const isGenerateBtn = clickedElement && (
            clickedElement.id === 'generateAgendaBtn' ||
            (clickedElement.classList && clickedElement.classList.contains('btn') && 
             clickedElement.textContent && clickedElement.textContent.includes('AI生成议程'))
        );
        
        if (!isGenerateBtn) {
            return; // 不是目标按钮，直接返回
        }
        
        // 确保点击发生在预约模态框内
        const bookingModal = document.getElementById('bookingModal');
        if (!bookingModal || bookingModal.style.display !== 'block') {
            return; // 模态框未打开，忽略点击
        }
        
        event.preventDefault();
        event.stopPropagation();
        
        console.log('🎯 检测到AI生成议程按钮点击');
        
        const subject = document.getElementById('subject');
        const subjectValue = subject ? subject.value.trim() : '';
        
        if (!subjectValue) {
            showError('请先输入会议主题');
            return;
        }
        
        const btn = clickedElement;
        const originalText = btn.textContent;
        btn.disabled = true;
        btn.textContent = '生成中...';
        
        console.log('📝 开始生成议程，主题:', subjectValue);
        
        try {
            const response = await fetch(`${API_BASE}/bookings/agenda-suggestion`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify({ subject: subjectValue })
            });
            
            console.log('📡 API响应状态:', response.status);
            const result = await response.json();
            console.log('📦 API返回结果:', result);
            
            if (result.success && result.data && result.data.agenda) {
                // 直接自动填入备注字段，不弹出预览弹窗
                const remarkField = document.getElementById('remark');
                console.log('🔍 查找备注字段，元素:', remarkField);
                
                if (remarkField) {
                    const agendaContent = result.data.agenda;
                    
                    // 直接填入备注字段
                    remarkField.value = agendaContent;
                    
                    // 触发事件，确保表单验证等逻辑能正常工作
                    remarkField.dispatchEvent(new Event('input', { bubbles: true }));
                    remarkField.dispatchEvent(new Event('change', { bubbles: true }));
                    
                    // 滚动到备注字段，让用户看到填入的内容
                    setTimeout(() => {
                        remarkField.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
                        remarkField.focus();
                    }, 100);
                    
                    showSuccess('议程已自动填入备注字段');
                    console.log('✅ 议程已成功填入备注字段');
                    console.log('   内容长度:', agendaContent.length, '字符');
                    console.log('   内容预览:', agendaContent.substring(0, 150) + '...');
                    console.log('   备注字段当前值长度:', remarkField.value.length, '字符');
                    
                    // 验证填入是否成功
                    if (remarkField.value === agendaContent) {
                        console.log('✅ 验证通过：备注字段内容与API返回一致');
                    } else {
                        console.error('❌ 验证失败：备注字段内容与API返回不一致');
                        console.error('   期望长度:', agendaContent.length);
                        console.error('   实际长度:', remarkField.value.length);
                    }
                } else {
                    console.error('❌ 未找到备注字段，ID: remark');
                    // 尝试查找所有textarea元素
                    const allTextareas = document.querySelectorAll('textarea');
                    console.error('   找到的textarea元素数量:', allTextareas.length);
                    allTextareas.forEach((ta, index) => {
                        console.error(`   textarea[${index}]: id=${ta.id}, name=${ta.name}`);
                    });
                    showError('未找到备注字段');
                }
            } else {
                console.error('❌ API返回失败:', result);
                showError(result.message || 'AI服务暂时不可用，请稍后重试或手动输入备注');
            }
        } catch (error) {
            console.error('❌ AI生成议程请求失败:', error);
            console.error('   错误详情:', error.stack);
            showError('请求超时，请稍后重试或手动输入备注');
        } finally {
            btn.disabled = false;
            btn.textContent = originalText;
        }
    }, true); // 使用捕获阶段，确保能捕获到事件
    
    agendaGenerationHandlerBound = true;
    console.log('✅ AI生成议程事件监听器已绑定（使用document事件委托）');
}

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

// 确保AI生成议程按钮的事件监听器已绑定
setupAgendaGenerationHandler();

