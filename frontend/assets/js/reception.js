/**
 * MELIA HOTEL - Receptionist Operations Module
 * Comprehensive Front Desk Workflow:
 * 1. Receive PENDING bookings from customers
 * 2. Check details, confirm deposit/payment, assign specific physical room -> CONFIRMED
 * 3. Perform Check-in when guest arrives -> CHECKED_IN (Room OCCUPIED)
 * 4. Perform Check-out & Invoice settlement -> CHECKED_OUT (Room DIRTY)
 */

const Reception = {
  allBookings: [],
  currentFilter: 'ALL',
  searchKeyword: '',
  currentBooking: null,

  // Initialize and load all reception bookings
  async loadBookings() {
    const container = document.getElementById('receptionBookingsList');
    if (!container) return;

    try {
      const response = await apiRequest('/api/v1/reception/bookings');
      if (response && response.data) {
        this.allBookings = response.data;
        this.updateCounters();
        this.renderBookings();
      }
    } catch (error) {
      container.innerHTML = `
        <div class="p-8 bg-rose-50 border border-rose-200 rounded-2xl text-center text-rose-700">
          <p class="font-bold text-sm">Không thể tải danh sách đơn đặt phòng!</p>
          <p class="text-xs text-rose-500 mt-1">${error.message || 'Lỗi kết nối máy chủ'}</p>
        </div>
      `;
    }
  },

  // Update pending counter badge
  updateCounters() {
    const pendingCount = this.allBookings.filter(b => (b.status || b.trangThaiDon) === 'PENDING').length;
    const badge = document.getElementById('badgePendingCount');
    if (badge) {
      badge.innerText = pendingCount;
      if (pendingCount > 0) {
        badge.className = 'px-1.5 py-0.5 rounded-full bg-amber-500 text-white text-[10px] font-bold animate-pulse';
      } else {
        badge.className = 'px-1.5 py-0.5 rounded-full bg-amber-200 text-amber-900 text-[10px] font-bold';
      }
    }
  },

  // Filter bookings by tab
  filterBookings(status) {
    this.currentFilter = status;
    
    // Update button active states
    const filterKeys = ['ALL', 'PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT'];
    filterKeys.forEach(k => {
      const btn = document.getElementById(`recFilter_${k}`);
      if (!btn) return;
      if (k === status) {
        btn.className = 'px-3.5 py-2 rounded-xl font-bold bg-blue-950 text-white shadow-sm transition-all';
      } else {
        btn.className = 'px-3.5 py-2 rounded-xl font-bold bg-slate-100 text-slate-700 hover:bg-slate-200 transition-all';
      }
    });

    this.renderBookings();
  },

  // Search input handler
  onSearchInput(val) {
    this.searchKeyword = val.trim().toLowerCase();
    this.renderBookings();
  },

  // Render bookings cards list
  renderBookings() {
    const container = document.getElementById('receptionBookingsList');
    if (!container) return;

    let list = this.allBookings;

    // Filter by status
    if (this.currentFilter !== 'ALL') {
      list = list.filter(b => (b.status || b.trangThaiDon) === this.currentFilter);
    }

    // Filter by keyword
    if (this.searchKeyword) {
      const kw = this.searchKeyword;
      list = list.filter(b => {
        const id = (b.bookingId || b.maDatPhong || '').toLowerCase();
        const guest = (b.customerName || b.guestName || b.tenKhachHang || '').toLowerCase();
        const phone = (b.customerPhone || b.guestPhone || b.soDienThoaiKhachHang || '').toLowerCase();
        const cccd = (b.customerCccd || '').toLowerCase();
        return id.includes(kw) || guest.includes(kw) || phone.includes(kw) || cccd.includes(kw);
      });
    }

    if (list.length === 0) {
      container.innerHTML = `
        <div class="py-12 text-center text-slate-400 bg-slate-50/60 rounded-2xl border border-slate-200/60">
          <svg class="w-10 h-10 mx-auto text-slate-300 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/></svg>
          <p class="text-sm font-semibold text-slate-600">Không có đơn đặt phòng nào trong mục này</p>
          <p class="text-xs text-slate-400 mt-1">Đơn đặt phòng mới từ khách hàng sẽ hiển thị tại đây theo thời gian thực.</p>
        </div>
      `;
      return;
    }

    container.innerHTML = list.map(b => {
      const bookingId = b.bookingId || b.maDatPhong;
      const status = b.status || b.trangThaiDon || 'PENDING';
      const isPending = status === 'PENDING';
      const isConfirmed = status === 'CONFIRMED';
      const isCheckedIn = status === 'CHECKED_IN';
      const isCheckedOut = status === 'CHECKED_OUT';

      const roomTypeId = b.roomTypeId || b.maLoaiPhong || '';
      const roomTypeName = b.roomTypeName || b.tenLoaiPhong || 'Hạng phòng 5 sao';
      const checkInDate = b.checkInDate || b.thoiGianNhanPhong;
      const checkOutDate = b.checkOutDate || b.thoiGianTraPhong;
      const totalAmount = b.totalAmount !== undefined ? b.totalAmount : (b.tongTien !== undefined ? b.tongTien : 0);
      const bookingDate = b.bookingDate || b.ngayDat;
      const customerName = b.customerName || b.guestName || b.tenKhachHang || 'Khách vãng lai';
      const customerPhone = b.customerPhone || b.guestPhone || b.soDienThoaiKhachHang || 'N/A';
      const customerCccd = b.customerCccd || 'N/A';
      const assignedRoomNumber = b.assignedRoomNumber || b.soPhong;
      const assignedRoomId = b.roomId || b.maPhong || '';
      const specialRequests = b.specialRequests || b.yeuCauDb || '';

      // Status Badge Config
      let badgeHtml = '';
      if (isPending) {
        badgeHtml = `<span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-amber-100 text-amber-900 border border-amber-300 flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-amber-500 animate-ping"></span>Chờ Lễ Tân Tiếp Nhận (PENDING)</span>`;
      } else if (isConfirmed) {
        badgeHtml = `<span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-emerald-100 text-emerald-900 border border-emerald-300 flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-emerald-600"></span>Đã Gán Phòng & Xác Nhận (CONFIRMED)</span>`;
      } else if (isCheckedIn) {
        badgeHtml = `<span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-blue-100 text-blue-900 border border-blue-300 flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-blue-600"></span>Khách Đang Ở (CHECKED_IN)</span>`;
      } else if (isCheckedOut) {
        badgeHtml = `<span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-purple-100 text-purple-900 border border-purple-300">Đã Trả Phòng (CHECKED_OUT)</span>`;
      } else {
        badgeHtml = `<span class="px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider bg-slate-100 text-slate-700">${status}</span>`;
      }

      return `
        <div class="p-5 rounded-2xl bg-white border ${isPending ? 'border-amber-300 shadow-md ring-1 ring-amber-100' : 'border-slate-200/90 shadow-sm'} hover:shadow-md transition-all">
          <div class="flex flex-col lg:flex-row lg:items-center justify-between pb-4 border-b border-slate-100 gap-3">
            <div class="flex flex-wrap items-center gap-3">
              <span class="font-mono text-base font-bold text-blue-950">${bookingId}</span>
              ${badgeHtml}
              <span class="text-xs text-slate-400">Tạo: ${formatDateTime(bookingDate)}</span>
            </div>

            <!-- Contextual Action Buttons -->
            <div class="flex items-center gap-2">
              ${isPending ? `
                <button onclick="Reception.openConfirmModal('${bookingId}', '${roomTypeId}', '${escapeHtml(roomTypeName)}', '${escapeHtml(customerName)}', ${totalAmount})" class="btn-gold text-xs px-4 py-2 flex items-center gap-1.5 shadow-md">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>
                  Gán Phòng & Xác Nhận (CONFIRMED)
                </button>
              ` : ''}

              ${isConfirmed ? `
                <button onclick="Reception.openCheckInModal('${bookingId}', '${roomTypeId}', '${assignedRoomId}', '${assignedRoomNumber || ''}')" class="bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs px-4 py-2 rounded-full shadow-md flex items-center gap-1.5 transition-all">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
                  Check-in (Khách Vào Ở)
                </button>
              ` : ''}

              ${isCheckedIn ? `
                <button onclick="Reception.openCheckOutModal('${bookingId}')" class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-xs px-4 py-2 rounded-full shadow-md flex items-center gap-1.5 transition-all">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
                  Check-out & Quyết Toán
                </button>
              ` : ''}

              ${isCheckedOut ? `
                <span class="text-xs px-3 py-1.5 bg-slate-100 text-slate-500 rounded-xl font-semibold">Đã hoàn tất thanh toán & trả phòng</span>
              ` : ''}
            </div>
          </div>

          <!-- 3-Column Info Summary -->
          <div class="grid grid-cols-1 md:grid-cols-3 gap-5 pt-4 text-xs">
            <!-- Col 1: Customer -->
            <div class="space-y-1">
              <span class="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">Thông Tin Khách Hàng</span>
              <div class="text-sm font-bold text-slate-800">${customerName}</div>
              <div class="text-slate-600">SĐT: <span class="font-mono font-medium">${customerPhone}</span></div>
              <div class="text-slate-600">CCCD: <span class="font-mono font-medium">${customerCccd}</span></div>
            </div>

            <!-- Col 2: Room & Stay Dates -->
            <div class="space-y-1">
              <span class="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">Hạng Phòng & Lưu Trú</span>
              <div class="text-sm font-bold text-blue-900">${roomTypeName}</div>
              <div class="text-slate-600">Thời gian: <span class="font-medium">${formatDate(checkInDate)}</span> ➔ <span class="font-medium">${formatDate(checkOutDate)}</span></div>
              ${assignedRoomNumber ? `
                <div class="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-800 font-bold mt-1">
                  <svg class="w-3.5 h-3.5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/></svg>
                  Phòng đã gán: Số ${assignedRoomNumber}
                </div>
              ` : `
                <div class="inline-flex items-center gap-1 px-2 py-0.5 rounded text-[11px] font-semibold bg-amber-50 text-amber-800 border border-amber-200 mt-1">
                  ⚠️ Chưa gán phòng cụ thể
                </div>
              `}
            </div>

            <!-- Col 3: Finance & Notes -->
            <div class="space-y-1">
              <span class="text-[11px] font-bold uppercase tracking-wider text-slate-400 block">Tài Chính & Ghi Chú</span>
              <div class="text-base font-bold font-sans text-amber-700">${formatVND(totalAmount)}</div>
              ${specialRequests ? `<div class="text-slate-500 italic">Yêu cầu: "${specialRequests}"</div>` : `<div class="text-slate-400 italic">Không có yêu cầu đặc biệt</div>`}
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  // Open Modal: Gán phòng & Xác nhận đặt phòng (CONFIRMED)
  async openConfirmModal(bookingId, roomTypeId, roomTypeName, customerName, totalAmount) {
    document.getElementById('confirmBookingId').value = bookingId;
    document.getElementById('confirmDisplayId').innerText = bookingId;
    document.getElementById('confirmRoomTypeId').value = roomTypeId;
    document.getElementById('confirmCustomerName').innerText = customerName;
    document.getElementById('confirmRoomTypeName').innerText = roomTypeName;
    document.getElementById('confirmTotalAmount').innerText = formatVND(totalAmount);
    document.getElementById('confirmDepositAmount').value = '';
    document.getElementById('confirmNotes').value = '';

    const select = document.getElementById('confirmRoomSelect');
    select.innerHTML = '<option value="">Đang tìm phòng sạch còn trống thuộc hạng này...</option>';
    openModal('receptionConfirmModal');

    try {
      const response = await apiRequest('/api/v1/housekeeping/rooms?status=AVAILABLE');
      if (response && response.data) {
        const rooms = response.data;
        // Filter rooms matching exact room type
        const matchingRooms = rooms.filter(r => (r.loaiPhong && r.loaiPhong.maLoaiPhong === roomTypeId) || !roomTypeId);

        if (matchingRooms.length === 0) {
          select.innerHTML = '<option value="">Không có phòng sạch (AVAILABLE) nào thuộc hạng phòng này!</option>';
          showToast(`Cảnh báo: Hiện không còn phòng trống nào thuộc hạng ${roomTypeName}!`, 'warning');
        } else {
          select.innerHTML = '<option value="">-- Chọn số phòng cụ thể để gán cho khách --</option>' + 
            matchingRooms.map(r => `
              <option value="${r.maPhong}">
                Phòng ${r.soPhong} - Tầng ${r.tang} (${r.loaiPhong ? r.loaiPhong.tenLoaiPhong : 'Sẵn sàng'})
              </option>
            `).join('');
        }
      }
    } catch (e) {
      select.innerHTML = '<option value="">Lỗi tải danh sách phòng vật lý!</option>';
    }
  },

  // Submit: Gán phòng & Xác nhận đặt phòng (CONFIRMED)
  async submitConfirmAndAssign() {
    const maDatPhong = document.getElementById('confirmBookingId')?.value;
    const maPhongCuThe = document.getElementById('confirmRoomSelect')?.value;
    const tienCocStr = document.getElementById('confirmDepositAmount')?.value;
    const ghiChu = document.getElementById('confirmNotes')?.value;

    if (!maPhongCuThe) {
      showToast('Vui lòng chọn số phòng cụ thể để gán cho khách!', 'warning');
      return;
    }

    const payload = {
      maDatPhong: maDatPhong,
      maPhongCuThe: maPhongCuThe,
      tienCoc: tienCocStr ? parseFloat(tienCocStr) : 0,
      ghiChu: ghiChu || null
    };

    try {
      const response = await apiRequest('/api/v1/reception/confirm', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      if (response && response.success) {
        showToast(`Đã gán phòng và xác nhận đặt phòng thành công cho đơn ${maDatPhong}!`, 'success');
        closeModal('receptionConfirmModal');
        await this.loadBookings();
      }
    } catch (error) {
      showToast(error.message || 'Xác nhận đặt phòng thất bại!', 'error');
    }
  },

  // Open Modal: Check-in (Khách vào ở)
  async openCheckInModal(bookingId, roomTypeId, assignedRoomId, assignedRoomNumber) {
    document.getElementById('checkInBookingId').value = bookingId;
    document.getElementById('checkInDisplayId').innerText = bookingId;

    const preassignedBox = document.getElementById('checkInPreassignedBox');
    const preassignedText = document.getElementById('checkInPreassignedRoom');
    const select = document.getElementById('checkInRoomSelect');

    if (assignedRoomNumber) {
      if (preassignedBox) preassignedBox.classList.remove('hidden');
      if (preassignedText) preassignedText.innerText = `Phòng Số ${assignedRoomNumber}`;
    } else {
      if (preassignedBox) preassignedBox.classList.add('hidden');
    }

    select.innerHTML = '<option value="">Đang tải danh sách phòng...</option>';
    openModal('receptionCheckInModal');

    try {
      const response = await apiRequest('/api/v1/housekeeping/rooms?status=AVAILABLE');
      if (response && response.data) {
        const rooms = response.data;
        const validRooms = rooms.filter(r => (r.loaiPhong && r.loaiPhong.maLoaiPhong === roomTypeId) || !roomTypeId);

        let optionsHtml = '';
        if (assignedRoomNumber) {
          optionsHtml += `<option value="${assignedRoomId || ''}" selected>-- Giữ phòng đã gán: Số ${assignedRoomNumber} --</option>`;
        } else {
          optionsHtml += `<option value="">-- Chọn số phòng cụ thể --</option>`;
        }

        optionsHtml += validRooms.map(r => `
          <option value="${r.maPhong}">
            Phòng ${r.soPhong} (Tầng ${r.tang})
          </option>
        `).join('');

        select.innerHTML = optionsHtml;
      }
    } catch (e) {
      select.innerHTML = '<option value="">Lỗi tải danh sách phòng!</option>';
    }
  },

  // Submit: Check-in (Khách vào ở)
  async submitCheckIn() {
    const maDatPhong = document.getElementById('checkInBookingId')?.value;
    const maPhongCuThe = document.getElementById('checkInRoomSelect')?.value;

    const payload = {
      maDatPhong: maDatPhong,
      maPhongCuThe: maPhongCuThe || null
    };

    try {
      const response = await apiRequest('/api/v1/reception/check-in', {
        method: 'POST',
        body: JSON.stringify(payload)
      });

      if (response && response.success) {
        showToast(`Thủ tục Check-in thành công! Khách đã nhận phòng và vào ở.`, 'success');
        closeModal('receptionCheckInModal');
        await this.loadBookings();
        // Also refresh Housekeeping room grid if available
        if (typeof Housekeeping !== 'undefined' && Housekeeping.loadRooms) {
          Housekeeping.loadRooms();
        }
      }
    } catch (error) {
      showToast(error.message || 'Thủ tục Check-in thất bại!', 'error');
    }
  },

  // Open Modal: Check-out & Quyết toán
  async openCheckOutModal(bookingId) {
    document.getElementById('checkOutBookingId').value = bookingId;
    document.getElementById('checkOutDisplayId').innerText = bookingId;
    document.getElementById('checkOutSurcharge').value = '0';

    openModal('receptionCheckOutModal');
    
    // Find booking from memory
    const b = this.allBookings.find(x => (x.bookingId || x.maDatPhong) === bookingId);
    if (b) {
      this.currentBooking = b;
      this.calculateCheckOutSummary();
    }
  },

  calculateCheckOutSummary() {
    if (!this.currentBooking) return;

    const b = this.currentBooking;
    const roomCharge = b.totalAmount || b.tongTien || 0;
    const serviceCharge = 0;
    const surcharge = parseFloat(document.getElementById('checkOutSurcharge')?.value || '0') || 0;
    const discount = 0;

    const finalAmount = roomCharge + serviceCharge + surcharge - discount;
    const daThanhToan = roomCharge; // Default advance paid
    const conPhaiTra = Math.max(0, finalAmount - daThanhToan);

    document.getElementById('summaryRoomCharge').innerText = formatVND(roomCharge);
    document.getElementById('summaryServiceCharge').innerText = formatVND(serviceCharge);
    document.getElementById('summaryFinalAmount').innerText = formatVND(finalAmount);
    document.getElementById('summaryDaThanhToan').innerText = `-${formatVND(daThanhToan)}`;
    document.getElementById('summaryConPhaiTra').innerText = formatVND(conPhaiTra);

    const payMethodSection = document.getElementById('checkOutPayMethodSection');
    if (payMethodSection) {
      if (conPhaiTra === 0) {
        payMethodSection.classList.add('opacity-50', 'pointer-events-none');
      } else {
        payMethodSection.classList.remove('opacity-50', 'pointer-events-none');
      }
    }
  },

  // Submit: Check-out
  async submitCheckOut() {
    const maDatPhong = document.getElementById('checkOutBookingId')?.value;
    const phuThuPhatSinh = parseFloat(document.getElementById('checkOutSurcharge')?.value || '0') || 0;
    const phuongThucThanhToan = document.getElementById('checkOutPayMethodSelect')?.value || 'CASH';

    try {
      const response = await apiRequest('/api/v1/reception/check-out', {
        method: 'POST',
        body: JSON.stringify({
          maDatPhong,
          phuThuPhatSinh,
          phuongThucThanhToan
        })
      });

      if (response && response.success) {
        const inv = response.data;
        showToast('Thủ tục Check-out & Quyết toán hóa đơn hoàn tất!', 'success');
        closeModal('receptionCheckOutModal');
        this.renderPrintableInvoice(inv);
        await this.loadBookings();
        // Refresh room grid (room is now DIRTY)
        if (typeof Housekeeping !== 'undefined' && Housekeeping.loadRooms) {
          Housekeeping.loadRooms();
        }
      }
    } catch (error) {
      showToast(error.message || 'Thủ tục Check-out thất bại!', 'error');
    }
  },

  // Render Printable Invoice Modal
  renderPrintableInvoice(inv) {
    document.getElementById('invNumber').innerText = inv.invoiceId;
    document.getElementById('invDate').innerText = formatDateTime(inv.issueDate);
    document.getElementById('invBookingId').innerText = inv.bookingId;
    document.getElementById('invRoomCharge').innerText = formatVND(inv.roomCharge);
    document.getElementById('invServiceCharge').innerText = formatVND(inv.serviceCharge);
    document.getElementById('invSurcharge').innerText = formatVND(inv.surcharge);
    document.getElementById('invDiscount').innerText = `-${formatVND(inv.discount)}`;
    document.getElementById('invTotalAmount').innerText = formatVND(inv.finalAmount);
    document.getElementById('invDaThanhToan').innerText = formatVND(inv.daThanhToan || 0);
    document.getElementById('invConPhaiTra').innerText = formatVND(inv.conPhaiTra || 0);
    document.getElementById('invPayMethod').innerText = inv.paymentMethod || 'TIỀN MẶT';
    document.getElementById('invPayStatus').innerText = inv.paymentStatus || 'PAID';

    openModal('invoiceModal');
  }
};

// Helper HTML escaper
function escapeHtml(str) {
  if (!str) return '';
  return str.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
}
