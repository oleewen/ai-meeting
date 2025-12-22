/**
 * 个人预约页面脚本
 * 处理预约列表查询、筛选、取消、签到等功能
 */

const BookingPage = {
    // 当前预约列表
    currentBookings: [],
    
    /**
     * 初始化个人预约页面
     */
    init: function() {
        this.bindEvents();
        this.loadMyBookings();
    },

    /**
     * 绑定事件
     */
    bindEvents: function() {
        // 快速操作按钮
        document.getElementById('todayBookingsBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.loadTodayBookings();
        });

        document.getElementById('upcomingBookingsBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.loadUpcomingBookings();
        });

        document.getElementById('historyBookingsBtn').addEventListener('click', (e) => {
            e.preventDefault();
            this.loadHistoryBookings();
        });

        // 筛选按钮
        document.getElementById('filterBookingsBtn').addEventListener('click', () => {
            this.performFilter();
        });

        // 重置筛选
        document.getElementById('resetFilterBtn').addEventListener('click', () => {
            this.resetFilter();
        });
    },

    /**
     * 加载我的所有预约
     */
    loadMyBookings: async function() {
        const bookingsList = document.getElementById('bookingsList');
        Utils.showLoading(bookingsList);
        
        try {
            const response = await Http.get('/bookings/my-bookings');
            this.currentBookings = response.bookings || [];
            this.renderBookings(this.currentBookings);
            this.updateBookingCount(this.currentBookings.length);
        } catch (error) {
            console.error('加载预约失败:', error);
            Utils.showError('加载预约失败，请稍后重试');
            bookingsList.innerHTML = '';
        }
    },

    /**
     * 加载今日预约
     */
    loadTodayBookings: async function() {
        const bookingsList = document.getElementById('bookingsList');
        Utils.showLoading(bookingsList);
        
        try {
            const response = await Http.get('/bookings/today');
            this.currentBookings = response.bookings || [];
            this.renderBookings(this.currentBookings);
            this.updateBookingCount(this.currentBookings.length);
        } catch (error) {
            console.error('加载今日预约失败:', error);
            Utils.showError('加载今日预约失败，请稍后重试');
            bookingsList.innerHTML = '';
        }
    },

    /**
     * 加载即将到来的预约
     */
    loadUpcomingBookings: async function() {
        const bookingsList = document.getElementById('bookingsList');
        Utils.showLoading(bookingsList);
        
        try {
            const response = await Http.get('/bookings/upcoming');
            this.currentBookings = response.bookings || [];
            this.renderBookings(this.currentBookings);
            this.updateBookingCount(this.currentBookings.length);
        } catch (error) {
            console.error('加载即将到来的预约失败:', error);
            Utils.showError('加载即将到来的预约失败，请稍后重试');
            bookingsList.innerHTML = '';
        }
    },

    /**
     * 加载历史预约
     */
    loadHistoryBookings: async function() {
        const bookingsList = document.getElementById('bookingsList');
        Utils.showLoading(bookingsList);
        
        try {
            const response = await Http.get('/bookings/history');
            this.currentBookings = response.bookings || [];
            this.renderBookings(this.currentBookings);
            this.updateBookingCount(this.currentBookings.length);
        } catch (error) {
            console.error('加载历史预约失败:', error);
            Utils.showError('加载历史预约失败，请稍后重试');
            bookingsList.innerHTML = '';
        }
    },

    /**
     * 执行筛选
     */
    performFilter: async function() {
        const filterData = this.getFilterData();
        const bookingsList = document.getElementById('bookingsList');
        
        Utils.showLoading(bookingsList);
        
        try {
            const response = await Http.get('/bookings/my-bookings', filterData);
            this.currentBookings = response.bookings || [];
            this.renderBookings(this.currentBookings);
            this.updateBookingCount(this.currentBookings.length);
        } catch (error) {
            console.error('筛选预约失败:', error);
            Utils.showError('筛选预约失败，请检查筛选条件后重试');
            bookingsList.innerHTML = '';
        }
    },

    /**
     * 获取筛选数据
     */
    getFilterData: function() {
        const filterData = {};
        
        const startDate = document.getElementById('filterStartDate').value;
        const endDate = document.getElementById('filterEndDate').value;
        const status = document.getElementById('filterStatus').value;
        
        if (startDate) filterData.startDate = startDate;
        if (endDate) filterData.endDate = endDate;
        if (status) filterData.status = status;
        
        return filterData;
    },

    /**
     * 重置筛选
     */
    resetFilter: function() {
        document.getElementById('filterStartDate').value = '';
        document.getElementById('filterEndDate').value = '';
        document.getElementById('filterStatus').value = '';
        
        this.loadMyBookings();
    },

    /**
     * 渲染预约列表
     */
    renderBookings: function(bookings) {
        const bookingsList = document.getElementById('bookingsList');
        
        if (!bookings || bookings.length === 0) {
            Utils.showEmpty(bookingsList, '暂无预约记录');
            return;
        }
        
        const bookingsHtml = bookings.map(booking => this.createBookingItem(booking)).join('');
        bookingsList.innerHTML = bookingsHtml;
        
        // 绑定操作按钮事件
        this.bindBookingActions();
    },

    /**
     * 创建预约项HTML
     */
    createBookingItem: function(booking) {
        const statusClass = booking.status.toLowerCase();
        const statusName = Utils.getStatusName(booking.status);
        
        // 判断可用操作
        const now = new Date();
        const startTime = new Date(booking.startTime);
        const canCancel = booking.status === 'ACTIVE' && startTime > new Date(now.getTime() + 2 * 60 * 60 * 1000); // 提前2小时
        const canCheckIn = booking.status === 'ACTIVE' && 
                          startTime <= new Date(now.getTime() + 15 * 60 * 1000) && // 开始前15分钟
                          startTime >= new Date(now.getTime() - 30 * 60 * 1000);   // 开始后30分钟
        
        const actionsHtml = this.createActionButtons(booking, canCancel, canCheckIn);
        
        return `
            <div class="booking-item" data-booking-id="${booking.bookingId}">
                <div class="booking-header">
                    <div>
                        <div class="booking-title">${booking.subject}</div>
                        <div class="booking-room">${booking.roomName} - ${booking.roomLocation}</div>
                    </div>
                    <div class="booking-status ${statusClass}">${statusName}</div>
                </div>
                
                <div class="booking-details">
                    <span>📅 ${Utils.formatDateTime(booking.startTime)} - ${Utils.formatTime(booking.endTime)}</span>
                    ${booking.checkedInAt ? `<span>✅ 签到时间: ${Utils.formatDateTime(booking.checkedInAt)}</span>` : ''}
                </div>
                
                ${actionsHtml ? `<div class="booking-actions">${actionsHtml}</div>` : ''}
            </div>
        `;
    },

    /**
     * 创建操作按钮
     */
    createActionButtons: function(booking, canCancel, canCheckIn) {
        const buttons = [];
        
        if (canCheckIn) {
            buttons.push(`<button class="btn btn-success btn-checkin" data-booking-id="${booking.bookingId}">签到</button>`);
        }
        
        if (canCancel) {
            buttons.push(`<button class="btn btn-danger btn-cancel" data-booking-id="${booking.bookingId}">取消</button>`);
        }
        
        buttons.push(`<button class="btn btn-secondary btn-detail" data-booking-id="${booking.bookingId}">详情</button>`);
        
        return buttons.join('');
    },

    /**
     * 绑定预约操作事件
     */
    bindBookingActions: function() {
        // 签到按钮
        document.querySelectorAll('.btn-checkin').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const bookingId = btn.getAttribute('data-booking-id');
                this.checkInBooking(bookingId);
            });
        });

        // 取消按钮
        document.querySelectorAll('.btn-cancel').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const bookingId = btn.getAttribute('data-booking-id');
                this.cancelBooking(bookingId);
            });
        });

        // 详情按钮
        document.querySelectorAll('.btn-detail').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const bookingId = btn.getAttribute('data-booking-id');
                this.showBookingDetail(bookingId);
            });
        });
    },

    /**
     * 签到预约
     */
    checkInBooking: async function(bookingId) {
        if (!confirm('确认要签到吗？')) {
            return;
        }
        
        try {
            const response = await Http.post(`/bookings/${bookingId}/checkin`);
            Utils.showSuccess(response.message || '签到成功');
            
            // 刷新列表
            this.loadMyBookings();
        } catch (error) {
            console.error('签到失败:', error);
            Utils.showError(error.message || '签到失败，请稍后重试');
        }
    },

    /**
     * 取消预约
     */
    cancelBooking: async function(bookingId) {
        if (!confirm('确认要取消这个预约吗？取消后不可恢复。')) {
            return;
        }
        
        try {
            const response = await Http.delete(`/bookings/${bookingId}`);
            Utils.showSuccess(response.message || '预约已取消');
            
            // 刷新列表
            this.loadMyBookings();
        } catch (error) {
            console.error('取消预约失败:', error);
            Utils.showError(error.message || '取消预约失败，请稍后重试');
        }
    },

    /**
     * 显示预约详情
     */
    showBookingDetail: async function(bookingId) {
        const modal = document.getElementById('roomDetailModal');
        const modalTitle = document.getElementById('modalRoomName');
        const modalContent = document.getElementById('roomDetailContent');
        
        modalTitle.textContent = '预约详情';
        Utils.showLoading(modalContent);
        
        Modal.show('roomDetailModal');
        
        try {
            const response = await Http.get(`/bookings/${bookingId}`);
            this.renderBookingDetail(response, modalContent);
            
            // 隐藏预约按钮（因为这是查看已有预约的详情）
            document.getElementById('bookRoomBtn').style.display = 'none';
        } catch (error) {
            console.error('获取预约详情失败:', error);
            modalContent.innerHTML = '<div class="error-message">获取预约详情失败</div>';
        }
    },

    /**
     * 渲染预约详情
     */
    renderBookingDetail: function(bookingDetail, container) {
        const statusName = Utils.getStatusName(bookingDetail.status);
        const statusClass = bookingDetail.status.toLowerCase();
        
        container.innerHTML = `
            <div class="booking-detail">
                <div class="detail-section">
                    <h4>预约信息</h4>
                    <p><strong>会议主题:</strong> ${bookingDetail.subject}</p>
                    <p><strong>会议室:</strong> ${bookingDetail.roomName}</p>
                    <p><strong>位置:</strong> ${bookingDetail.roomLocation}</p>
                    <p><strong>时间:</strong> ${Utils.formatDateTime(bookingDetail.startTime)} - ${Utils.formatTime(bookingDetail.endTime)}</p>
                    <p><strong>参会人数:</strong> ${bookingDetail.attendeeCount}人</p>
                    <p><strong>状态:</strong> <span class="booking-status ${statusClass}">${statusName}</span></p>
                    ${bookingDetail.notes ? `<p><strong>备注:</strong> ${bookingDetail.notes}</p>` : ''}
                </div>
                
                ${bookingDetail.checkedInAt ? `
                <div class="detail-section">
                    <h4>签到信息</h4>
                    <p><strong>签到时间:</strong> ${Utils.formatDateTime(bookingDetail.checkedInAt)}</p>
                </div>
                ` : ''}
                
                <div class="detail-section">
                    <h4>可用操作</h4>
                    <div class="booking-actions">
                        ${bookingDetail.canCheckIn ? `<button class="btn btn-success" onclick="BookingPage.checkInBooking('${bookingDetail.bookingId}'); Modal.hide('roomDetailModal');">签到</button>` : ''}
                        ${bookingDetail.canCancel ? `<button class="btn btn-danger" onclick="BookingPage.cancelBooking('${bookingDetail.bookingId}'); Modal.hide('roomDetailModal');">取消预约</button>` : ''}
                        ${!bookingDetail.canCheckIn && !bookingDetail.canCancel ? '<p style="color: #666;">暂无可用操作</p>' : ''}
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * 更新预约计数
     */
    updateBookingCount: function(count) {
        const bookingCount = document.getElementById('bookingCount');
        bookingCount.textContent = `共 ${count} 条预约`;
    }
};