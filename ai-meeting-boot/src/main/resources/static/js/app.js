/**
 * 主应用程序脚本
 * 处理页面导航、通用功能等
 */

// 应用程序配置
const APP_CONFIG = {
    API_BASE_URL: '/api',
    CURRENT_USER_ID: 'user-002', // 模拟当前用户ID，实际应用中应从认证系统获取
    DATE_FORMAT: 'YYYY-MM-DD',
    DATETIME_FORMAT: 'YYYY-MM-DDTHH:mm'
};

// 通用工具函数
const Utils = {
    /**
     * 格式化日期时间
     */
    formatDateTime: function(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        return date.toLocaleString('zh-CN', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * 格式化日期
     */
    formatDate: function(date) {
        if (!date) return '';
        const d = new Date(date);
        return d.toLocaleDateString('zh-CN');
    },

    /**
     * 格式化时间
     */
    formatTime: function(dateTime) {
        if (!dateTime) return '';
        const date = new Date(dateTime);
        return date.toLocaleTimeString('zh-CN', {
            hour: '2-digit',
            minute: '2-digit'
        });
    },

    /**
     * 设备名称映射
     */
    getEquipmentName: function(equipment) {
        const names = {
            'PROJECTOR': '投影仪',
            'VIDEO_CONFERENCE': '视频会议',
            'WHITEBOARD': '白板',
            'LARGE_SCREEN': '大屏幕'
        };
        return names[equipment] || equipment;
    },

    /**
     * 预约状态名称映射
     */
    getStatusName: function(status) {
        const names = {
            'ACTIVE': '有效',
            'CANCELLED': '已取消',
            'CHECKED_IN': '已签到'
        };
        return names[status] || status;
    },

    /**
     * 显示错误消息
     */
    showError: function(message, container = null) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message';
        errorDiv.textContent = message;
        
        if (container) {
            container.insertBefore(errorDiv, container.firstChild);
        } else {
            document.querySelector('.main-content').insertBefore(errorDiv, document.querySelector('.main-content').firstChild);
        }
        
        // 5秒后自动移除
        setTimeout(() => {
            if (errorDiv.parentNode) {
                errorDiv.parentNode.removeChild(errorDiv);
            }
        }, 5000);
    },

    /**
     * 显示成功消息
     */
    showSuccess: function(message, container = null) {
        const successDiv = document.createElement('div');
        successDiv.className = 'success-message';
        successDiv.textContent = message;
        
        if (container) {
            container.insertBefore(successDiv, container.firstChild);
        } else {
            document.querySelector('.main-content').insertBefore(successDiv, document.querySelector('.main-content').firstChild);
        }
        
        // 3秒后自动移除
        setTimeout(() => {
            if (successDiv.parentNode) {
                successDiv.parentNode.removeChild(successDiv);
            }
        }, 3000);
    },

    /**
     * 显示加载状态
     */
    showLoading: function(container) {
        container.innerHTML = '<div class="loading">加载中...</div>';
    },

    /**
     * 显示空状态
     */
    showEmpty: function(container, message = '暂无数据') {
        container.innerHTML = `
            <div class="empty-state">
                <h3>${message}</h3>
                <p>请尝试调整搜索条件</p>
            </div>
        `;
    }
};

// HTTP请求工具
const Http = {
    /**
     * 检查fetch API支持
     */
    _checkFetchSupport: function() {
        if (!window.fetch) {
            throw new Error('您的浏览器不支持现代网络请求功能，请升级浏览器');
        }
    },

    /**
     * 创建查询字符串（兼容旧浏览器）
     */
    _createQueryString: function(params) {
        if (window.URLSearchParams) {
            return new URLSearchParams(params).toString();
        } else {
            // 手动构建查询字符串
            const pairs = [];
            for (const key in params) {
                if (params.hasOwnProperty(key) && params[key] !== null && params[key] !== undefined) {
                    pairs.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
                }
            }
            return pairs.join('&');
        }
    },

    /**
     * 发送GET请求
     */
    get: async function(url, params = {}) {
        this._checkFetchSupport();
        
        const queryString = this._createQueryString(params);
        const fullUrl = `${APP_CONFIG.API_BASE_URL}${url}${queryString ? '?' + queryString : ''}`;
        
        try {
            const response = await fetch(fullUrl, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': APP_CONFIG.CURRENT_USER_ID
                }
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('GET request failed:', error);
            throw error;
        }
    },

    /**
     * 发送POST请求
     */
    post: async function(url, data = {}) {
        const fullUrl = `${APP_CONFIG.API_BASE_URL}${url}`;
        
        try {
            const response = await fetch(fullUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': APP_CONFIG.CURRENT_USER_ID
                },
                body: JSON.stringify(data)
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('POST request failed:', error);
            throw error;
        }
    },

    /**
     * 发送DELETE请求
     */
    delete: async function(url) {
        const fullUrl = `${APP_CONFIG.API_BASE_URL}${url}`;
        
        try {
            const response = await fetch(fullUrl, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-User-Id': APP_CONFIG.CURRENT_USER_ID
                }
            });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
        } catch (error) {
            console.error('DELETE request failed:', error);
            throw error;
        }
    }
};

// 页面导航管理
const Navigation = {
    init: function() {
        // 绑定导航链接点击事件
        document.querySelectorAll('.nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const page = link.getAttribute('data-page');
                this.showPage(page);
                
                // 更新导航状态
                document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
                link.classList.add('active');
            });
        });
    },

    showPage: function(pageId) {
        // 隐藏所有页面
        document.querySelectorAll('.page').forEach(page => {
            page.classList.remove('active');
        });
        
        // 显示目标页面
        const targetPage = document.getElementById(`${pageId}-page`);
        if (targetPage) {
            targetPage.classList.add('active');
            
            // 触发页面特定的初始化逻辑
            this.triggerPageInit(pageId);
        }
    },

    triggerPageInit: function(pageId) {
        switch (pageId) {
            case 'search':
                if (typeof SearchPage !== 'undefined') {
                    SearchPage.init();
                }
                break;
            case 'booking':
                if (typeof BookingPage !== 'undefined') {
                    BookingPage.init();
                }
                break;
            case 'create':
                if (typeof CreatePage !== 'undefined') {
                    CreatePage.init();
                }
                break;
        }
    }
};

// 模态框管理
const Modal = {
    show: function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.add('show');
            document.body.style.overflow = 'hidden';
        }
    },

    hide: function(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.classList.remove('show');
            document.body.style.overflow = '';
        }
    },

    init: function() {
        // 绑定关闭按钮事件
        document.querySelectorAll('.modal .close, .modal .btn-secondary').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const modal = btn.closest('.modal');
                if (modal) {
                    this.hide(modal.id);
                }
            });
        });

        // 点击背景关闭模态框
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.hide(modal.id);
                }
            });
        });
    }
};

// 浏览器兼容性检查
const BrowserCheck = {
    init: function() {
        this.checkBasicSupport();
        this.addPolyfills();
        this.checkModernFeatures();
    },

    checkBasicSupport: function() {
        // 检查基本的JavaScript功能
        if (!document.querySelector || !document.addEventListener) {
            this.showUnsupportedBrowser();
            return false;
        }
        return true;
    },

    addPolyfills: function() {
        // Array.from polyfill
        if (!Array.from) {
            Array.from = function(arrayLike) {
                const result = [];
                for (let i = 0; i < arrayLike.length; i++) {
                    result.push(arrayLike[i]);
                }
                return result;
            };
        }

        // Object.assign polyfill
        if (!Object.assign) {
            Object.assign = function(target) {
                for (let i = 1; i < arguments.length; i++) {
                    const source = arguments[i];
                    for (const key in source) {
                        if (source.hasOwnProperty(key)) {
                            target[key] = source[key];
                        }
                    }
                }
                return target;
            };
        }

        // String.prototype.trim polyfill
        if (!String.prototype.trim) {
            String.prototype.trim = function() {
                return this.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g, '');
            };
        }
    },

    checkModernFeatures: function() {
        const warnings = [];
        
        if (!window.fetch) {
            warnings.push('网络请求功能');
        }
        
        if (!window.Promise) {
            warnings.push('异步处理功能');
        }
        
        if (!window.localStorage) {
            warnings.push('本地存储功能');
        }
        
        if (warnings.length > 0) {
            console.warn('您的浏览器不支持以下现代功能：', warnings.join('、'));
            this.showCompatibilityWarning(warnings);
        }
    },

    showUnsupportedBrowser: function() {
        document.body.innerHTML = `
            <div style="padding: 2rem; text-align: center; font-family: Arial, sans-serif;">
                <h1 style="color: #e74c3c;">浏览器不支持</h1>
                <p>抱歉，您的浏览器版本过旧，无法正常使用本系统。</p>
                <p>请升级到以下浏览器的最新版本：</p>
                <ul style="text-align: left; display: inline-block; margin: 1rem 0;">
                    <li>Chrome 60+</li>
                    <li>Firefox 55+</li>
                    <li>Safari 11+</li>
                    <li>Edge 79+</li>
                </ul>
            </div>
        `;
    },

    showCompatibilityWarning: function(warnings) {
        const warningDiv = document.createElement('div');
        warningDiv.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            background: #f39c12;
            color: white;
            padding: 0.75rem;
            text-align: center;
            z-index: 9999;
            font-size: 0.9rem;
        `;
        warningDiv.innerHTML = `
            ⚠️ 您的浏览器版本较旧，部分功能可能无法正常使用。建议升级浏览器以获得最佳体验。
            <button onclick="this.parentNode.remove()" style="margin-left: 1rem; background: none; border: 1px solid white; color: white; padding: 0.25rem 0.5rem; cursor: pointer;">关闭</button>
        `;
        document.body.insertBefore(warningDiv, document.body.firstChild);
    }
};

// 应用程序初始化
document.addEventListener('DOMContentLoaded', function() {
    // 首先检查浏览器兼容性
    BrowserCheck.init();
    
    // 初始化导航
    Navigation.init();
    
    // 初始化模态框
    Modal.init();
    
    // 初始化搜索页面（默认页面）
    if (typeof SearchPage !== 'undefined') {
        SearchPage.init();
    }
    
    console.log('会议室预约系统已初始化');
});