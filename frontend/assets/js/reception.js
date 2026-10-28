/**
 * MELIA HOTEL - Receptionist Operations Module
 * Booking lookup, Check-in room assignment with room-type validation,
 * Check-out invoice calculation with advance payment deduction (da_thanh_toan) and print layout.
 */

const Reception = {
  currentBooking: null,

  // Search booking by ID or CCCD
  async searchBooking() {
    const query = document.getElementById('receptionSearchInput')?.value.trim();
    if (!query) {
      showToast('Vui lòng nhập Mã đặt phòng hoặc số CCCD của khách hàng!', 'warning');
      return;
    }

    const container = document.getElementById('receptionSearchResult');
    if (!container) return;

    container.innerHTML = `
      <div class="py-12 text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-blue-900 border-t-transparent"></div>
        <p class="mt-3 text-slate-500 text-sm">Đang tra cứu cơ sở dữ liệu...</p>
      </div>
    `;

    try {
      const response = await apiRequest(`/api/v1/reception/bookings/${encodeURIComponent(query)}`);
      if (response && response.data) {
        this.currentBooking = response.data;
        this.renderBookingResult(response.data);
      }
    } catch (error) {
      container.innerHTML = `
        <div class="p-6 bg-rose-50 border border-rose-200 rounded-xl text-center text-rose-700">
          <p class="font-semibold">${error.message || 'Không tìm thấy đơn đặt phòng tương ứng!'}</p>
        </div>
      `;
    }
  },

  renderBookingResult(b) {
    const container = document.getElementById('receptionSearchResult');
    if (!container) return;

    const bookingId = b.bookingId || b.maDatPhong;
    const status = b.status || b.trangThaiDon || 'PENDING';
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
    const couponCode = b.couponCode || b.maPhieuGg;
    const specialRequests = b.specialRequests || b.yeuCauDb;

    container.innerHTML = `
      <div class="p-6 rounded-2xl bg-white border border-slate-200/90 shadow-md">
        <div class="flex flex-col lg:flex-row lg:items-center justify-between pb-4 border-b border-slate-100 gap-4">
          <div>
            <div class="flex items-center gap-3">
              <h3 class="font-mono text-lg font-bold text-blue-950">${bookingId}</h3>
              <span class="text-xs px-3 py-1 rounded-full font-bold uppercase tracking-wider ${
                isConfirmed ? 'bg-emerald-100 text-emerald-800' :
                isCheckedIn ? 'bg-blue-100 text-blue-800' :
                isCheckedOut ? 'bg-purple-100 text-purple-800' :
                'bg-amber-100 text-amber-800'
              }">
                ${status}
              </span>
            </div>
            <p class="text-xs text-slate-400 mt-1">Ngày tạo đơn: ${formatDateTime(bookingDate)}</p>
          </div>

          <!-- Actions -->
          <div class="flex items-center gap-3">
            ${isConfirmed ? `
              <button onclick="Reception.openCheckInModal('${bookingId}', '${roomTypeId}')" class="btn-gold text-xs px-4 py-2.5">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1"/></svg>
                Làm thủ tục Check-in
              </button>
            ` : ''}

            ${isCheckedIn ? `
              <button onclick="Reception.openCheckOutModal('${bookingId}')" class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold text-xs px-4 py-2.5 rounded-full shadow-md flex items-center gap-1.5 transition-all">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/></svg>
                Làm thủ tục Check-out & Quyết toán
              </button>
            ` : ''}

            ${isCheckedOut ? `
              <span class="text-xs px-3 py-1.5 bg-slate-100 text-slate-500 rounded-lg font-medium">Đơn đã hoàn tất thủ tục</span>
            ` : ''}
          </div>
        </div>

        <!-- Info Grid -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 pt-5">
          <div class="space-y-2">
            <span class="text-xs uppercase tracking-wider font-semibold text-slate-400 block">Khách Hàng</span>
            <div class="text-sm font-bold text-slate-800">${customerName}</div>
            <div class="text-xs text-slate-600">SĐT: ${customerPhone}</div>
            <div class="text-xs text-slate-600">Số CCCD: <span class="font-mono font-medium">${customerCccd}</span></div>
          </div>

          <div class="space-y-2">
            <span class="text-xs uppercase tracking-wider font-semibold text-slate-400 block">Lưu Trú & Phòng</span>
            <div class="text-sm font-bold text-slate-800">${roomTypeName}</div>
            <div class="text-xs text-slate-600">Nhận: <span class="font-medium">${formatDate(checkInDate)}</span></div>
            <div class="text-xs text-slate-600">Trả: <span class="font-medium">${formatDate(checkOutDate)}</span></div>
            ${assignedRoomNumber ? `
              <div class="text-xs font-bold text-emerald-700 bg-emerald-50 px-2 py-1 rounded w-fit">
                Phòng đã gán: Số ${assignedRoomNumber}
              </div>
            ` : '<div class="text-xs text-amber-600 italic">Chưa gán phòng cụ thể</div>'}
          </div>

          <div class="space-y-2">
            <span class="text-xs uppercase tracking-wider font-semibold text-slate-400 block">Tài Chính & Ghi Chú</span>
            <div class="text-lg font-bold font-sans text-blue-950">${formatVND(totalAmount)}</div>
            ${couponCode ? `<div class="text-xs text-amber-700">Voucher: <span class="font-mono">${couponCode}</span></div>` : ''}
            <div class="text-xs text-slate-500 italic">Yêu cầu: ${specialRequests || 'Không có yêu cầu đặc biệt'}</div>
          </div>
        </div>
      </div>
    `;
  },

  // Open Check-in Modal and list available physical rooms
  async openCheckInModal(bookingId, roomTypeId) {
    document.getElementById('checkInBookingId').value = bookingId;
    document.getElementById('checkInDisplayId').innerText = bookingId;

    const select = document.getElementById('checkInRoomSelect');
    select.innerHTML = '<option value="">Đang tìm phòng sạch còn trống...</option>';
    openModal('receptionCheckInModal');

    try {
      // Fetch available rooms from housekeeping
      const response = await apiRequest('/api/v1/housekeeping/rooms?status=AVAILABLE');
      if (response && response.data && response.data.length > 0) {
        // Filter by matching room type if present
        let matchedRooms = response.data;
        if (roomTypeId) {
          const typeFiltered = response.data.filter(r => r.loaiPhong && r.loaiPhong.maLoaiPhong === roomTypeId);
          if (typeFiltered.length > 0) {
            matchedRooms = typeFiltered;
          }
        }

        select.innerHTML = matchedRooms.map(r => `
          <option value="${r.maPhong}">Phòng ${r.soPhong} (Tầng ${r.tang} - ${r.loaiPhong?.tenLoaiPhong || ''})</option>
        `).join('');
      } else {
        select.innerHTML = '<option value="">Không có phòng trống nào sẵn sàng!</option>';
      }
    } catch (e) {
      select.innerHTML = '<option value="">Lỗi tải danh sách phòng</option>';
    }
  },

  // Submit Check-in
  async submitCheckIn() {
    const maDatPhong = document.getElementById('checkInBookingId')?.value;
    const maPhongCuThe = document.getElementById('checkInRoomSelect')?.value;

    if (!maPhongCuThe) {
      showToast('Vui lòng chọn số phòng để gán cho khách!', 'warning');
      return;
    }

    try {
      const response = await apiRequest('/api/v1/reception/check-in', {
        method: 'POST',
        body: JSON.stringify({ maDatPhong, maPhongCuThe })
      });

      if (response && response.success) {
        showToast(`Thủ tục Check-in thành công! Đã gán phòng cho đơn ${maDatPhong}.`, 'success');
        closeModal('receptionCheckInModal');
        this.searchBooking(); // Reload booking
      }
    } catch (error) {
      showToast(error.message || 'Check-in thất bại!', 'error');
    }
  },

  // Open Check-out Modal and compute balance with advance deduction
  async openCheckOutModal(bookingId) {
    document.getElementById('checkOutBookingId').value = bookingId;
    document.getElementById('checkOutDisplayId').innerText = bookingId;
    document.getElementById('checkOutSurcharge').value = '0';

    openModal('receptionCheckOutModal');
    this.calculateCheckOutSummary();
  },

  calculateCheckOutSummary() {
    if (!this.currentBooking) return;

    const b = this.currentBooking;
    const roomCharge = b.totalAmount || 0;
    const serviceCharge = 0; // Loaded or added via minibar
    const surcharge = parseFloat(document.getElementById('checkOutSurcharge')?.value || '0') || 0;
    const discount = 0;

    const finalAmount = Math.max(0, roomCharge + serviceCharge + surcharge - discount);
    
    // Check if advance payment was made
    const daThanhToan = (b.status === 'CONFIRMED' || b.status === 'CHECKED_IN') ? roomCharge : 0;
    const conPhaiTra = Math.max(0, finalAmount - daThanhToan);

    document.getElementById('summaryRoomCharge').innerText = formatVND(roomCharge);
    document.getElementById('summaryServiceCharge').innerText = formatVND(serviceCharge);
    document.getElementById('summarySurcharge').innerText = formatVND(surcharge);
    document.getElementById('summaryFinalAmount').innerText = formatVND(finalAmount);
    document.getElementById('summaryDaThanhToan').innerText = formatVND(daThanhToan);
    document.getElementById('summaryConPhaiTra').innerText = formatVND(conPhaiTra);

    // If remaining balance is 0, update payment method text
    const payMethodSection = document.getElementById('checkOutPayMethodSection');
    if (payMethodSection) {
      if (conPhaiTra === 0) {
        payMethodSection.classList.add('opacity-50', 'pointer-events-none');
      } else {
        payMethodSection.classList.remove('opacity-50', 'pointer-events-none');
      }
    }
  },

  // Submit Check-out
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
        this.searchBooking();
      }
    } catch (error) {
      showToast(error.message || 'Check-out thất bại!', 'error');
    }
  },

  // Render Printable Invoice Modal
  renderPrintableInvoice(inv) {
    document.getElementById('invNumber').innerText = inv.invoiceId;
    document.getElementById('invBookingId').innerText = inv.bookingId;
    document.getElementById('invDate').innerText = formatDateTime(inv.issueDate);
    document.getElementById('invRoomCharge').innerText = formatVND(inv.roomCharge);
    document.getElementById('invServiceCharge').innerText = formatVND(inv.serviceCharge);
    document.getElementById('invSurcharge').innerText = formatVND(inv.surcharge);
    document.getElementById('invDiscount').innerText = `-${formatVND(inv.discount)}`;
    document.getElementById('invTotalAmount').innerText = formatVND(inv.finalAmount);
    document.getElementById('invPaidAdvance').innerText = formatVND(inv.daThanhToan);
    document.getElementById('invBalanceDue').innerText = formatVND(inv.conPhaiTra);
    document.getElementById('invPayMethod').innerText = inv.paymentMethod || 'CASH';
    document.getElementById('invPayStatus').innerText = inv.paymentStatus || 'PAID';

    openModal('invoiceModal');
  },

  printInvoice() {
    window.print();
  }
};
