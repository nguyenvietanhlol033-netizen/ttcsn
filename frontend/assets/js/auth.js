/**
 * MELIA HOTEL - Authentication & RBAC State Management Module
 */

const Auth = {
  // Get current logged-in user details
  getUser() {
    const raw = localStorage.getItem('melia_user');
    return raw ? JSON.parse(raw) : null;
  },

  // Get current JWT token
  getToken() {
    return localStorage.getItem('melia_token');
  },

  // Check login state
  isLoggedIn() {
    return !!this.getToken() && !!this.getUser();
  },

  // Role checks
  getRole() {
    const user = this.getUser();
    return user ? user.role : null;
  },

  isCustomer() {
    return this.getRole() === 'ROLE_CUSTOMER';
  },

  isReceptionist() {
    const r = this.getRole();
    return r === 'ROLE_RECEPTIONIST' || r === 'ROLE_ADMIN';
  },

  isHousekeeping() {
    const r = this.getRole();
    return r === 'ROLE_HOUSEKEEPING' || r === 'ROLE_ADMIN';
  },

  isAdmin() {
    return this.getRole() === 'ROLE_ADMIN';
  },

  isAccountant() {
    const r = this.getRole();
    return r === 'ROLE_ACCOUNTANT' || r === 'ROLE_ADMIN';
  },

  isStaff() {
    const r = this.getRole();
    return ['ROLE_ADMIN', 'ROLE_RECEPTIONIST', 'ROLE_HOUSEKEEPING', 'ROLE_ACCOUNTANT'].includes(r);
  },

  // Login handler
  async login(username, password) {
    try {
      const response = await apiRequest('/api/v1/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password })
      });

      if (response && response.success) {
        const authData = response.data;
        localStorage.setItem('melia_token', authData.accessToken);
        localStorage.setItem('melia_user', JSON.stringify({
          userId: authData.userId,
          username: authData.username,
          fullName: authData.fullName,
          role: authData.role
        }));

        showToast(`Xin chào ${authData.fullName}! Đăng nhập thành công.`, 'success');
        this.updateUI();

        // If employee logs in on index.html, prompt or redirect to admin.html
        if (this.isStaff() && window.location.pathname.endsWith('index.html') || window.location.pathname.endsWith('/')) {
          setTimeout(() => {
            if (confirm('Bạn đang đăng nhập bằng tài khoản nhân viên. Bạn có muốn chuyển sang Cổng Quản trị Nghiệp vụ không?')) {
              window.location.href = 'admin.html';
            }
          }, 800);
        }

        return authData;
      }
    } catch (error) {
      showToast(error.message || 'Đăng nhập thất bại. Vui lòng kiểm tra lại!', 'error');
      throw error;
    }
  },

  // Register Customer handler
  async register(data) {
    try {
      const response = await apiRequest('/api/v1/auth/register', {
        method: 'POST',
        body: JSON.stringify(data)
      });

      if (response && response.success) {
        showToast('Đăng ký tài khoản thành công! Vui lòng đăng nhập.', 'success');
        return response.data;
      }
    } catch (error) {
      showToast(error.message || 'Đăng ký thất bại!', 'error');
      throw error;
    }
  },

  // Logout handler
  logout() {
    localStorage.removeItem('melia_token');
    localStorage.removeItem('melia_user');
    showToast('Đã đăng xuất khỏi hệ thống.', 'info');
    setTimeout(() => {
      window.location.href = 'index.html';
    }, 600);
  },

  // Update dynamic navigation UI
  updateUI() {
    const navAuthContainer = document.getElementById('navAuthContainer');
    if (!navAuthContainer) return;

    if (!this.isLoggedIn()) {
      navAuthContainer.innerHTML = `
        <button onclick="openModal('loginModal')" class="text-sm font-medium text-slate-700 hover:text-blue-900 transition-colors">
          Đăng nhập
        </button>
        <button onclick="openModal('registerModal')" class="btn-gold text-xs px-4 py-2">
          Đăng ký
        </button>
        <a href="admin.html" class="text-xs px-3 py-1.5 rounded-lg border border-slate-300 text-slate-600 hover:text-blue-900 hover:border-blue-900 transition-all flex items-center gap-1.5">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/></svg>
          Nhân viên
        </a>
      `;
    } else {
      const user = this.getUser();
      const roleBadge = this.getRoleBadgeText(user.role);
      const isStaffUser = this.isStaff();

      navAuthContainer.innerHTML = `
        <div class="flex items-center gap-3">
          ${isStaffUser ? `
            <a href="admin.html" class="btn-navy text-xs px-3.5 py-2 flex items-center gap-1.5 shadow-sm">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
              Bàn làm việc NV
            </a>
          ` : `
            <button onclick="Guest.openMyBookings()" class="text-sm font-medium text-slate-700 hover:text-blue-900 transition-colors flex items-center gap-1.5">
              <svg class="w-4 h-4 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/></svg>
              Đơn phòng của tôi
            </button>
          `}
          <div class="flex items-center gap-2 pl-3 border-l border-slate-200">
            <div class="w-8 h-8 rounded-full bg-blue-900 text-white font-semibold flex items-center justify-center text-xs shadow-inner">
              ${user.fullName ? user.fullName.charAt(0).toUpperCase() : 'U'}
            </div>
            <div class="text-left hidden sm:block">
              <div class="text-xs font-semibold text-slate-800 leading-tight">${user.fullName}</div>
              <div class="text-[10px] text-amber-700 font-medium">${roleBadge}</div>
            </div>
            <button onclick="Auth.logout()" title="Đăng xuất" class="p-1.5 text-slate-400 hover:text-rose-600 transition-colors rounded-lg hover:bg-rose-50">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
            </button>
          </div>
        </div>
      `;
    }
  },

  getRoleBadgeText(role) {
    switch (role) {
      case 'ROLE_ADMIN': return 'Ban Giám Đốc / Admin';
      case 'ROLE_RECEPTIONIST': return 'Lễ Tân Khách Sạn';
      case 'ROLE_HOUSEKEEPING': return 'Bộ Phận Buồng Phòng';
      case 'ROLE_ACCOUNTANT': return 'Bộ Phận Kế Toán';
      case 'ROLE_CUSTOMER': return 'Khách Hàng Thân Thiết';
      default: return 'Thành Viên';
    }
  }
};

// Initialize UI on load
document.addEventListener('DOMContentLoaded', () => {
  Auth.updateUI();
});
