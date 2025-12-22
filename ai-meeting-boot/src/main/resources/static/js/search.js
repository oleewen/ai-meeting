/**
 * 会议室搜索页面脚本
 * 处理会议室查询、筛选、排序和详情显示
 */

const SearchPage = {
    // 当前搜索结果
    currentResults: [],
    
    /**
     * 初始化搜索页面
     */
    init: function() {
        this.bindEvents();
        this.loadAllRooms();
        this.setDefaultDateTime();
    },

    /**
     * 绑定事件
     */
    bindEvents: function() {
        // 搜索按钮
        document.getElementById('searchBtn').addEventListener('click', () => {
            this.performSearch();
        });

        // 重置按钮
        document.getElementById('resetBtn').addEventListener('click', () => {
            this.resetForm();
        });

        // 模态框中的预约按钮
        document.getElementById('bookRoomBtn').addEventListener('click', () => {
            this.bookSelectedRoom();
        });
    },

    /**
     * 设置默认日期时间
     */
    setDefaultDateTime: function() {
        const now = new Date();
        const tomorrow = new Date(now);
        tomorrow.setDate(tomorrow.getDate() + 1);
        
        // 设置明天上午9点为默认开始时间
        tomorrow.setHours(9, 0, 0, 0);
        const startTime = tomorrow.toISOString().slice(0, 16);
        
        // 设置明天上午10点为默认结束时间
        tomorrow.setHours(10, 0, 0, 0);
        const endTime = tomorrow.toISOString().slice(0, 16);
        
        document.getElementById('startTime').value = startTime;
        document.getElementById('endTime').value = endTime;
    },

    /**
     * 加载所有活跃会议室
     */
    loadAllRooms: async function() {
        const roomsList = document.getElementById('roomsList');
        Utils.showLoading(roomsList);
        
        try {
            const response = await Http.get('/meeting-rooms');
            this.currentResults = response.rooms || [];
            this.renderRooms(this.currentResults);
            this.updateResultsCount(this.currentResults.length);
        } catch (error) {
            console.error('加载会议室失败:', error);
            Utils.showError('加载会议室失败，请稍后重试');
            roomsList.innerHTML = '';
        }
    },

    /**
     * 执行搜索
     */
    performSearch: async function() {
        const formData = this.getFormData();
        const roomsList = document.getElementById('roomsList');
        
        Utils.showLoading(roomsList);
        
        try {
            const response = await Http.get('/meeting-rooms/search', formData);
            this.currentResults = response.rooms || [];
            this.renderRooms(this.currentResults);
            this.updateResultsCount(this.currentResults.length);
        } catch (error) {
            console.error('搜索失败:', error);
            Utils.showError('搜索失败，请检查搜索条件后重试');
            roomsList.innerHTML = '';
        }
    },

    /**
     * 获取表单数据
     */
    getFormData: function() {
        const formData = {};
        
        // 时间参数
        const startTime = document.getElementById('startTime').value;
        const endTime = document.getElementById('endTime').value;
        if (startTime) formData.startTime = startTime;
        if (endTime) formData.endTime = endTime;
        
        // 地点参数
        const location = document.getElementById('location').value;
        if (location) formData.location = location;
        
        // 容量参数
        const minCapacity = document.getElementById('minCapacity').value;
        if (minCapacity) formData.minCapacity = parseInt(minCapacity);
        
        // 设备参数
        const selectedEquipments = Array.from(document.querySelectorAll('input[name="equipment"]:checked'))
            .map(cb => cb.value);
        if (selectedEquipments.length > 0) {
            formData.equipments = selectedEquipments.join(',');
        }
        
        // 排序参数
        const sortBy = document.getElementById('sortBy').value;
        if (sortBy) formData.sortBy = sortBy;
        
        return formData;
    },

    /**
     * 重置表单
     */
    resetForm: function() {
        document.getElementById('startTime').value = '';
        document.getElementById('endTime').value = '';
        document.getElementById('location').value = '';
        document.getElementById('minCapacity').value = '';
        document.getElementById('sortBy').value = '';
        
        // 取消所有设备选择
        document.querySelectorAll('input[name="equipment"]').forEach(cb => {
            cb.checked = false;
        });
        
        // 重新加载所有会议室
        this.loadAllRooms();
    },

    /**
     * 渲染会议室列表
     */
    renderRooms: function(rooms) {
        const roomsList = document.getElementById('roomsList');
        
        if (!rooms || rooms.length === 0) {
            Utils.showEmpty(roomsList, '未找到符合条件的会议室');
            return;
        }
        
        const roomsHtml = rooms.map(room => this.createRoomCard(room)).join('');
        roomsList.innerHTML = roomsHtml;
        
        // 绑定点击事件
        roomsList.querySelectorAll('.room-card').forEach((card, index) => {
            card.addEventListener('click', () => {
                this.showRoomDetail(rooms[index]);
            });
        });
    },

    /**
     * 创建会议室卡片HTML
     */
    createRoomCard: function(room) {
        const equipmentTags = room.equipments ? 
            room.equipments.map(eq => `<span class="equipment-tag">${Utils.getEquipmentName(eq)}</span>`).join('') : '';
        
        return `
            <div class="room-card" data-room-id="${room.id}">
                <div class="room-header">
                    <div>
                        <div class="room-name">${room.name}</div>
                        <div class="room-location">${room.location}</div>
                    </div>
                    <div class="room-capacity">容量: ${room.capacity}人</div>
                </div>
                <div class="room-equipments">
                    ${equipmentTags}
                </div>
            </div>
        `;
    },

    /**
     * 显示会议室详情
     */
    showRoomDetail: async function(room) {
        const modal = document.getElementById('roomDetailModal');
        const modalTitle = document.getElementById('modalRoomName');
        const modalContent = document.getElementById('roomDetailContent');
        
        modalTitle.textContent = room.name;
        Utils.showLoading(modalContent);
        
        Modal.show('roomDetailModal');
        
        try {
            // 获取今天的详细信息
            const today = new Date().toISOString().split('T')[0];
            const response = await Http.get(`/meeting-rooms/${room.id}`, { date: today });
            
            this.renderRoomDetail(response, modalContent);
            
            // 存储当前选中的会议室信息
            this.selectedRoom = room;
        } catch (error) {
            console.error('获取会议室详情失败:', error);
            modalContent.innerHTML = '<div class="error-message">获取会议室详情失败</div>';
        }
    },

    /**
     * 渲染会议室详情
     */
    renderRoomDetail: function(roomDetail, container) {
        const equipmentTags = roomDetail.equipments ? 
            roomDetail.equipments.map(eq => `<span class="equipment-tag">${Utils.getEquipmentName(eq)}</span>`).join('') : '';
        
        const timelineHtml = roomDetail.timeSlots && roomDetail.timeSlots.length > 0 ?
            roomDetail.timeSlots.map(slot => `
                <div class="timeline-item">
                    <div class="timeline-time">
                        ${Utils.formatTime(slot.startTime)} - ${Utils.formatTime(slot.endTime)}
                    </div>
                    <div class="timeline-content">
                        <div class="timeline-subject">${slot.subject}</div>
                        <div class="timeline-status">状态: ${Utils.getStatusName(slot.status)}</div>
                    </div>
                </div>
            `).join('') :
            '<div class="empty-state"><p>今日暂无预约</p></div>';
        
        container.innerHTML = `
            <div class="room-detail">
                <div class="detail-section">
                    <h4>基本信息</h4>
                    <p><strong>位置:</strong> ${roomDetail.location}</p>
                    <p><strong>容量:</strong> ${roomDetail.capacity}人</p>
                    <p><strong>设备:</strong></p>
                    <div class="room-equipments" style="margin-top: 0.5rem;">
                        ${equipmentTags || '<span style="color: #666;">无特殊设备</span>'}
                    </div>
                </div>
                
                <div class="detail-section">
                    <h4>今日排期 (${Utils.formatDate(roomDetail.date)})</h4>
                    <div class="timeline">
                        ${timelineHtml}
                    </div>
                </div>
            </div>
        `;
    },

    /**
     * 预约选中的会议室
     */
    bookSelectedRoom: function() {
        if (!this.selectedRoom) {
            Utils.showError('请先选择一个会议室');
            return;
        }
        
        // 关闭模态框
        Modal.hide('roomDetailModal');
        
        // 切换到创建预约页面，并预填会议室信息
        Navigation.showPage('create');
        
        // 如果创建预约页面已加载，预填信息
        setTimeout(() => {
            if (typeof CreatePage !== 'undefined' && CreatePage.prefillRoom) {
                CreatePage.prefillRoom(this.selectedRoom);
            }
        }, 100);
    },

    /**
     * 更新结果计数
     */
    updateResultsCount: function(count) {
        const resultsCount = document.getElementById('resultsCount');
        resultsCount.textContent = `共 ${count} 个会议室`;
    }
};