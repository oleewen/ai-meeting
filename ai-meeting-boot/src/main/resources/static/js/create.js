/**
 * 创建预约页面脚本
 * 处理预约创建表单和相关逻辑
 */

const CreatePage = {
    // 可用会议室列表
    availableRooms: [],
    
    /**
     * 初始化创建预约页面
     */
    init: function() {
        this.bindEvents();
        this.loadAvailableRooms();
        this.setDefaultDateTime();
    },

    /**
     * 绑定事件
     */
    bindEvents: function() {
        // 表单提交
        document.getElementById('createBookingForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.submitBooking();
        });

        // 取消按钮
        document.getElementById('cancelCreateBtn').addEventListener('click', () => {
            this.resetForm();
            Navigation.showPage('search');
        });

        // 时间变化时验证
        document.getElementById('bookingStartTime').addEventListener('change', () => {
            this.validateTimeRange();
        });
        
        document.getElementById('bookingEndTime').addEventListener('change', () => {
            this.validateTimeRange();
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
        
        document.getElementById('bookingStartTime').value = startTime;
        document.getElementById('bookingEndTime').value = endTime;
    },

    /**
     * 加载可用会议室
     */
    loadAvailableRooms: async function() {
        try {
            const response = await Http.get('/meeting-rooms');
            this.availableRooms = response.rooms || [];
            this.populateRoomSelect();
        } catch (error) {
            console.error('加载会议室失败:', error);
            Utils.showError('加载会议室失败，请稍后重试');
        }
    },

    /**
     * 填充会议室选择框
     */
    populateRoomSelect: function() {
        const select = document.getElementById('selectedRoom');
        
        // 清空现有选项（保留默认选项）
        while (select.children.length > 1) {
            select.removeChild(select.lastChild);
        }
        
        // 添加会议室选项
        this.availableRooms.forEach(room => {
            const option = document.createElement('option');
            option.value = room.id;
            option.textContent = `${room.name} (${room.location}, ${room.capacity}人)`;
            select.appendChild(option);
        });
    },

    /**
     * 预填会议室信息（从搜索页面跳转时使用）
     */
    prefillRoom: function(room) {
        const select = document.getElementById('selectedRoom');
        select.value = room.id;
        
        // 根据会议室容量设置建议参会人数
        const attendeeCountInput = document.getElementById('attendeeCount');
        if (!attendeeCountInput.value) {
            attendeeCountInput.value = Math.min(room.capacity, 8); // 默认不超过8人
        }
    },

    /**
     * 验证时间范围
     */
    validateTimeRange: function() {
        const startTime = document.getElementById('bookingStartTime').value;
        const endTime = document.getElementById('bookingEndTime').value;
        
        if (startTime && endTime) {
            const start = new Date(startTime);
            const end = new Date(end);
            const now = new Date();
            
            // 检查开始时间不能是过去时间
            if (start <= now) {
                Utils.showError('开始时间不能是过去时间');
                return false;
            }
            
            // 检查结束时间必须晚于开始时间
            if (end <= start) {
                Utils.showError('结束时间必须晚于开始时间');
                return false;
            }
            
            // 检查最短时长（30分钟）
            const duration = (end - start) / (1000 * 60); // 分钟
            if (duration < 30) {
                Utils.showError('会议时长不能少于30分钟');
                return false;
            }
            
            // 检查最长时长（8小时）
            if (duration > 480) {
                Utils.showError('会议时长不能超过8小时');
                return false;
            }
            
            // 检查工作时间（8:00-18:00，周一到周五）
            const startHour = start.getHours();
            const endHour = end.getHours();
            const dayOfWeek = start.getDay();
            
            if (dayOfWeek === 0 || dayOfWeek === 6) {
                Utils.showError('不能在周末预约会议室');
                return false;
            }
            
            if (startHour < 8 || endHour > 18 || (endHour === 18 && end.getMinutes() > 0)) {
                Utils.showError('预约时间必须在工作时间内（8:00-18:00）');
                return false;
            }
        }
        
        return true;
    },

    /**
     * 提交预约
     */
    submitBooking: async function() {
        // 验证表单
        if (!this.validateForm()) {
            return;
        }
        
        const formData = this.getFormData();
        const submitBtn = document.getElementById('submitBookingBtn');
        
        // 禁用提交按钮
        submitBtn.disabled = true;
        submitBtn.textContent = '创建中...';
        
        try {
            const response = await Http.post('/bookings', formData);
            
            Utils.showSuccess('预约创建成功！');
            
            // 重置表单
            this.resetForm();
            
            // 跳转到我的预约页面
            setTimeout(() => {
                Navigation.showPage('booking');
            }, 1500);
            
        } catch (error) {
            console.error('创建预约失败:', error);
            Utils.showError(error.message || '创建预约失败，请稍后重试');
        } finally {
            // 恢复提交按钮
            submitBtn.disabled = false;
            submitBtn.textContent = '创建预约';
        }
    },

    /**
     * 验证表单
     */
    validateForm: function() {
        // 检查必填字段
        const requiredFields = [
            { id: 'selectedRoom', name: '会议室' },
            { id: 'bookingStartTime', name: '开始时间' },
            { id: 'bookingEndTime', name: '结束时间' },
            { id: 'subject', name: '会议主题' },
            { id: 'attendeeCount', name: '参会人数' }
        ];
        
        for (const field of requiredFields) {
            const element = document.getElementById(field.id);
            if (!element.value.trim()) {
                Utils.showError(`请填写${field.name}`);
                element.focus();
                return false;
            }
        }
        
        // 验证时间范围
        if (!this.validateTimeRange()) {
            return false;
        }
        
        // 验证参会人数
        const attendeeCount = parseInt(document.getElementById('attendeeCount').value);
        if (attendeeCount <= 0) {
            Utils.showError('参会人数必须大于0');
            return false;
        }
        
        // 检查参会人数是否超过会议室容量
        const selectedRoomId = document.getElementById('selectedRoom').value;
        const selectedRoom = this.availableRooms.find(room => room.id === selectedRoomId);
        if (selectedRoom && attendeeCount > selectedRoom.capacity) {
            Utils.showError(`参会人数不能超过会议室容量（${selectedRoom.capacity}人）`);
            return false;
        }
        
        return true;
    },

    /**
     * 获取表单数据
     */
    getFormData: function() {
        return {
            meetingRoomId: document.getElementById('selectedRoom').value,
            startTime: document.getElementById('bookingStartTime').value,
            endTime: document.getElementById('bookingEndTime').value,
            subject: document.getElementById('subject').value.trim(),
            attendeeCount: parseInt(document.getElementById('attendeeCount').value),
            notes: document.getElementById('notes').value.trim()
        };
    },

    /**
     * 重置表单
     */
    resetForm: function() {
        document.getElementById('createBookingForm').reset();
        this.setDefaultDateTime();
        
        // 清除错误消息
        document.querySelectorAll('.error-message').forEach(msg => {
            msg.remove();
        });
    }
};