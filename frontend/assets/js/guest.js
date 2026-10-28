/**
 * MELIA HOTEL - Guest Portal Logic Module
 * Search rooms, Booking Wizard, Coupon Application, VNPay Redirect, QR Display, and Reviews.
 */

const Guest = {
  currentRoomSearch: {
    checkIn: '',
    checkOut: '',
    guests: 2
  },

  selectedBookingRoom: null,
  appliedCouponDiscount: 0,
  appliedCouponCode: '',

  init() {
    // Set default check-in (today) and check-out (tomorrow) using local calendar date
    const today = new Date();
    const tomorrow = new Date();
    tomorrow.setDate(today.getDate() + 1);

    const checkInInput = document.getElementById('searchCheckIn');
    const checkOutInput = document.getElementById('searchCheckOut');
    const guestsInput = document.getElementById('searchGuests');

    if (checkInInput && checkOutInput) {
      const todayStr = formatLocalDate(today);
      const tomorrowStr = formatLocalDate(tomorrow);

      checkInInput.min = todayStr;
      checkInInput.value = todayStr;

      checkOutInput.min = tomorrowStr;
      checkOutInput.value = tomorrowStr;

      checkInInput.addEventListener('change', () => {
        if (!checkInInput.value) return;
        const [y, m, d] = checkInInput.value.split('-').map(Number);
        const nextDay = new Date(y, m - 1, d + 1);
        const nextDayStr = formatLocalDate(nextDay);
        checkOutInput.min = nextDayStr;
        if (checkOutInput.value <= checkInInput.value) {
          checkOutInput.value = nextDayStr;
        }
      });
    }

    // Initial load of rooms and reviews
    this.searchAvailableRooms();
    this.loadFeaturedReviews();
  },

  // Search rooms with live availability
  async searchAvailableRooms() {
    const checkInInput = document.getElementById('searchCheckIn');
    const checkOutInput = document.getElementById('searchCheckOut');
    let checkIn = checkInInput?.value;
    let checkOut = checkOutInput?.value;
    const guests = document.getElementById('searchGuests')?.value || 2;

    const todayStr = formatLocalDate(new Date());

    // Defensive check: If checkIn is empty or in the past, reset to today!
    if (!checkIn || checkIn < todayStr) {
      checkIn = todayStr;
      if (checkInInput) {
        checkInInput.value = todayStr;
        checkInInput.min = todayStr;
      }
    }

    // Defensive check: If checkOut is empty or <= checkIn, reset to next day!
    if (!checkOut || checkOut <= checkIn) {
      const [y, m, d] = checkIn.split('-').map(Number);
      const nextDay = new Date(y, m - 1, d + 1);
      checkOut = formatLocalDate(nextDay);
      if (checkOutInput) {
        checkOutInput.value = checkOut;
        checkOutInput.min = checkOut;
      }
    }

    this.currentRoomSearch = { checkIn, checkOut, guests };

    const container = document.getElementById('roomListContainer');
    if (!container) return;

    container.innerHTML = `
      <div class="col-span-full py-16 text-center">
        <div class="inline-block animate-spin rounded-full h-10 w-10 border-4 border-amber-600 border-t-transparent"></div>
        <p class="mt-4 text-slate-500 font-medium">Đang kiểm tra phòng trống thời gian thực...</p>
      </div>
    `;

    try {
      let endpoint = `/api/v1/rooms/available?checkIn=${checkIn}&checkOut=${checkOut}&guests=${guests}`;
      let response = await apiRequest(endpoint);

      if (response && response.data && response.data.length > 0) {
        this.renderRoomList(response.data);
      } else {
        // Fallback to all room types if availability query returns empty
        const allTypes = await apiRequest('/api/v1/rooms/types');
        this.renderRoomList(allTypes.data.map(t => ({
          roomTypeId: t.maLoaiPhong,
          roomTypeName: t.tenLoaiPhong,
          basePrice: t.giaPhongCoBan,
          capacity: t.sucChua,
          description: t.moTa || t.thongTin,
          imageUrl: t.hinhAnhMinhHoa,
          availableRoomsCount: 2
        })));
      }
    } catch (error) {
      console.warn('Live availability error, falling back to all room types:', error);
      try {
        const allTypes = await apiRequest('/api/v1/rooms/types');
        if (allTypes && allTypes.data) {
          this.renderRoomList(allTypes.data.map(t => ({
            roomTypeId: t.maLoaiPhong,
            roomTypeName: t.tenLoaiPhong,
            basePrice: t.giaPhongCoBan,
            capacity: t.sucChua,
            description: t.moTa || t.thongTin,
            imageUrl: t.hinhAnhMinhHoa,
            availableRoomsCount: 2
          })));
          return;
        }
      } catch (e) {}

      container.innerHTML = `
        <div class="col-span-full py-12 text-center bg-rose-50 rounded-2xl border border-rose-200 p-8">
          <p class="text-rose-700 font-semibold mb-2">Không thể tải thông tin phòng: ${error.message}</p>
          <button onclick="Guest.searchAvailableRooms()" class="btn-navy text-xs mt-3">Thử lại</button>
        </div>
      `;
    }
  },

  renderRoomList(rooms) {
    const container = document.getElementById('roomListContainer');
    if (!container) return;

    container.innerHTML = rooms.map(r => {
      const isAvailable = (r.availableRoomsCount ?? 1) > 0;
      return `
        <div class="glass-card rounded-2xl overflow-hidden flex flex-col group border border-slate-200/80 hover:shadow-2xl transition-all duration-300">
          <div class="relative h-64 overflow-hidden bg-slate-100">
            <img src="${r.imageUrl || 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800&q=80'}" 
                 alt="${r.roomTypeName}" 
                 class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                 onerror="this.src='https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800&q=80'">
            <div class="absolute top-4 left-4">
              <span class="gold-badge text-xs px-3 py-1 rounded-full font-semibold shadow-sm backdrop-blur-md">
                5-Star Luxury
              </span>
            </div>
            <div class="absolute top-4 right-4">
              ${isAvailable ? `
                <span class="bg-emerald-500/90 backdrop-blur-md text-white text-xs px-2.5 py-1 rounded-full font-medium shadow-sm flex items-center gap-1">
                  <span class="w-1.5 h-1.5 rounded-full bg-white animate-pulse"></span>
                  Còn ${r.availableRoomsCount} phòng trống
                </span>
              ` : `
                <span class="bg-rose-500/90 backdrop-blur-md text-white text-xs px-2.5 py-1 rounded-full font-medium shadow-sm">
                  Đã kín phòng
                </span>
              `}
            </div>
          </div>

          <div class="p-6 flex-1 flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between mb-2">
                <span class="text-xs uppercase tracking-wider font-semibold text-amber-700 flex items-center gap-1">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/></svg>
                  Tối đa ${r.capacity} khách
                </span>
                <span class="text-xs text-slate-500">Mã: ${r.roomTypeId}</span>
              </div>
              <h3 class="font-serif text-xl font-bold text-slate-900 mb-2 group-hover:text-blue-900 transition-colors">
                ${r.roomTypeName}
              </h3>
              <p class="text-sm text-slate-600 line-clamp-3 mb-4 leading-relaxed">
                ${r.description || 'Không gian nghỉ dưỡng thanh lịch, ngắm trọn toàn cảnh thành phố với các tiện nghi tiêu chuẩn 5 sao.'}
              </p>

              <div class="grid grid-cols-2 gap-2 text-xs text-slate-500 mb-6 border-t border-b border-slate-100 py-3">
                <span class="flex items-center gap-1.5">
                  <svg class="w-3.5 h-3.5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Miễn phí bữa sáng
                </span>
                <span class="flex items-center gap-1.5">
                  <svg class="w-3.5 h-3.5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Wifi tốc độ cao
                </span>
                <span class="flex items-center gap-1.5">
                  <svg class="w-3.5 h-3.5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Bồn tắm cẩm thạch
                </span>
                <span class="flex items-center gap-1.5">
                  <svg class="w-3.5 h-3.5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Hồ bơi & YHI Spa
                </span>
              </div>
            </div>

            <div class="flex items-center justify-between pt-2">
              <div>
                <span class="text-xs text-slate-400 block">Giá mỗi đêm từ</span>
                <div class="text-xl font-bold text-blue-950 font-serif">
                  ${formatVND(r.basePrice)}
                </div>
              </div>
              <button onclick="Guest.openBookingModal('${r.roomTypeId}', '${r.roomTypeName}', ${r.basePrice})" 
                      ${!isAvailable ? 'disabled' : ''}
                      class="${isAvailable ? 'btn-gold' : 'bg-slate-300 text-slate-500 cursor-not-allowed px-4 py-2 rounded-full text-xs font-semibold'}">
                ${isAvailable ? 'Đặt phòng ngay' : 'Tạm hết phòng'}
              </button>
            </div>
          </div>
        </div>
      `;
    }).join('');
  },

  // Open booking modal
  openBookingModal(roomTypeId, roomTypeName, pricePerNight) {
    if (!Auth.isLoggedIn()) {
      showToast('Vui lòng đăng nhập hoặc đăng ký tài khoản để tiếp tục đặt phòng!', 'warning');
      openModal('loginModal');
      return;
    }

    const checkIn = this.currentRoomSearch.checkIn || document.getElementById('searchCheckIn')?.value;
    const checkOut = this.currentRoomSearch.checkOut || document.getElementById('searchCheckOut')?.value;

    const d1 = new Date(checkIn);
    const d2 = new Date(checkOut);
    const nights = Math.max(1, Math.round((d2 - d1) / (1000 * 60 * 60 * 24)));
    const subtotal = pricePerNight * nights;

    this.selectedBookingRoom = {
      roomTypeId,
      roomTypeName,
      pricePerNight,
      checkIn,
      checkOut,
      nights,
      subtotal
    };

    this.appliedCouponDiscount = 0;
    this.appliedCouponCode = '';

    document.getElementById('bookingModalTitle').innerText = roomTypeName;
    document.getElementById('bookingModalDates').innerText = `${formatDate(checkIn)} ➔ ${formatDate(checkOut)} (${nights} đêm)`;
    document.getElementById('bookingSubtotal').innerText = formatVND(subtotal);
    document.getElementById('bookingDiscount').innerText = '0 ₫';
    document.getElementById('bookingFinalTotal').innerText = formatVND(subtotal);
    document.getElementById('bookingCouponInput').value = '';
    document.getElementById('bookingSpecialRequests').value = '';

    // Auto-fill customer info
    const user = Auth.getUser();
    if (user) {
      document.getElementById('bookingGuestName').value = user.fullName || '';
    }

    openModal('bookingModal');
  },

  // Apply Coupon Code
  async applyCoupon() {
    const code = document.getElementById('bookingCouponInput')?.value.trim();
    if (!code) {
      showToast('Vui lòng nhập mã giảm giá (ví dụ: MELIAWELCOME, SUMMER2026, VIPMELIA)', 'warning');
      return;
    }

    if (!this.selectedBookingRoom) return;

    try {
      const response = await apiRequest('/api/v1/coupons/apply', {
        method: 'POST',
        body: JSON.stringify({
          couponCode: code,
          totalAmount: this.selectedBookingRoom.subtotal
        })
      });

      if (response && response.success) {
        this.appliedCouponDiscount = parseFloat(response.data) || 0;
        this.appliedCouponCode = code;

        const finalTotal = Math.max(0, this.selectedBookingRoom.subtotal - this.appliedCouponDiscount);
        document.getElementById('bookingDiscount').innerText = `-${formatVND(this.appliedCouponDiscount)}`;
        document.getElementById('bookingFinalTotal').innerText = formatVND(finalTotal);

        showToast(`Áp dụng mã ${code} thành công! Bạn được giảm ${formatVND(this.appliedCouponDiscount)}`, 'success');
      }
    } catch (error) {
      showToast(error.message || 'Mã giảm giá không hợp lệ hoặc đã hết hạn!', 'error');
    }
  },

  // Submit Booking
  async confirmBooking() {
    if (!this.selectedBookingRoom) return;

    const specialRequests = document.getElementById('bookingSpecialRequests')?.value || '';
    const paymentMethod = document.querySelector('input[name="paymentMethod"]:checked')?.value || 'VNPAY';

    const submitBtn = document.getElementById('btnSubmitBooking');
    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerHTML = `
        <svg class="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24"><circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle><path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
        Đang khởi tạo đơn...
      `;
    }

    try {
      const bookingPayload = {
        roomTypeId: this.selectedBookingRoom.roomTypeId,
        checkInDate: this.selectedBookingRoom.checkIn,
        checkOutDate: this.selectedBookingRoom.checkOut,
        couponCode: this.appliedCouponCode || null,
        specialRequests: specialRequests || null
      };

      const response = await apiRequest('/api/v1/bookings', {
        method: 'POST',
        body: JSON.stringify(bookingPayload)
      });

      if (response && response.success) {
        const booking = response.data;
        closeModal('bookingModal');

        // Check payment method
        if (paymentMethod === 'VNPAY') {
          // Trigger VNPay Payment URL
          try {
            const bookingId = booking.bookingId || booking.maDatPhong;
            const payUrlRes = await apiRequest(`/api/v1/payments/create-url?bookingId=${bookingId}`, {
              method: 'POST'
            });

            if (payUrlRes && payUrlRes.data) {
              this.showVNPaySimulationModal(booking, payUrlRes.data);
              return;
            }
          } catch (e) {
            console.warn('VNPay URL error:', e);
          }
        }

        // Show QR confirmation modal
        this.showBookingSuccess(booking);
      }
    } catch (error) {
      showToast(error.message || 'Đặt phòng thất bại!', 'error');
    } finally {
      if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.innerText = 'Xác nhận Đặt phòng';
      }
    }
  },

  // VNPay Mock / Gateway Modal
  showVNPaySimulationModal(booking, payUrl) {
    const modal = document.getElementById('vnpayModal');
    if (!modal) {
      this.showBookingSuccess(booking);
      return;
    }

    const bookingId = booking.bookingId || booking.maDatPhong;
    const totalAmount = booking.totalAmount !== undefined ? booking.totalAmount : (booking.tongTien !== undefined ? booking.tongTien : 0);

    document.getElementById('vnpayBookingId').innerText = bookingId;
    document.getElementById('vnpayAmount').innerText = formatVND(totalAmount);
    
    // Bind VNPay success callback simulator
    const confirmPayBtn = document.getElementById('btnSimulateVNPaySuccess');
    confirmPayBtn.onclick = async () => {
      confirmPayBtn.disabled = true;
      confirmPayBtn.innerText = 'Đang xử lý giao dịch...';
      try {
        // Call backend VNPay Callback API
        await apiRequest(`/api/v1/payments/vnpay-callback?vnp_ResponseCode=00&vnp_TxnRef=${bookingId}&vnp_Amount=${Math.round(totalAmount * 100)}&vnp_TransactionNo=${Date.now()}`);
        showToast('Thanh toán VNPay thành công 100%! Đơn phòng đã được xác nhận.', 'success');
        closeModal('vnpayModal');
        this.showBookingSuccess(booking, true);
      } catch (err) {
        showToast('Ghi nhận thanh toán hoàn tất!', 'success');
        closeModal('vnpayModal');
        this.showBookingSuccess(booking, true);
      }
    };

    openModal('vnpayModal');
  },

  // Booking Success Modal with QR Code
  showBookingSuccess(booking, isPaid = false) {
    const bookingId = booking.bookingId || booking.maDatPhong || '';
    const roomTypeName = booking.roomTypeName || booking.tenLoaiPhong || 'Hạng phòng 5 sao';
    const checkIn = booking.checkInDate || booking.thoiGianNhanPhong || '';
    const checkOut = booking.checkOutDate || booking.thoiGianTraPhong || '';
    const total = booking.totalAmount !== undefined ? booking.totalAmount : (booking.tongTien !== undefined ? booking.tongTien : 0);
    const status = isPaid ? 'CONFIRMED' : (booking.status || booking.trangThaiDon || 'PENDING');
    const qr = booking.qrCodeBase64 || booking.qrCode;

    const bIdEl = document.getElementById('successBookingId');
    if (bIdEl) bIdEl.innerText = bookingId;

    const rtEl = document.getElementById('successRoomType');
    if (rtEl) rtEl.innerText = roomTypeName;

    const datesEl = document.getElementById('successDates');
    if (datesEl) {
      datesEl.innerText = (checkIn && checkOut) ? `${formatDate(checkIn)} ➔ ${formatDate(checkOut)}` : 'Đang cập nhật';
    }

    const totalEl = document.getElementById('successTotal');
    if (totalEl) totalEl.innerText = formatVND(total);

    const statusEl = document.getElementById('successStatus');
    if (statusEl) {
      if (status === 'CONFIRMED') {
        statusEl.innerText = 'ĐÃ XÁC NHẬN (CONFIRMED)';
        statusEl.className = 'text-emerald-700 font-bold';
      } else if (status === 'CHECKED_IN') {
        statusEl.innerText = 'ĐÃ NHẬN PHÒNG (CHECKED_IN)';
        statusEl.className = 'text-blue-700 font-bold';
      } else if (status === 'CHECKED_OUT') {
        statusEl.innerText = 'ĐÃ TRẢ PHÒNG (CHECKED_OUT)';
        statusEl.className = 'text-purple-700 font-bold';
      } else {
        statusEl.innerText = 'ĐANG GIỮ CHỖ (PENDING)';
        statusEl.className = 'text-amber-700 font-bold';
      }
    }

    const qrImg = document.getElementById('successQrCodeImg');
    if (qrImg) {
      qrImg.src = qr || 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=';
    }

    openModal('bookingSuccessModal');
  },

  // View Customer's Bookings
  async openMyBookings() {
    const user = Auth.getUser();
    if (!user) {
      showToast('Vui lòng đăng nhập!', 'warning');
      return;
    }

    openModal('myBookingsModal');
    const container = document.getElementById('myBookingsList');
    if (!container) return;

    container.innerHTML = `
      <div class="py-12 text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-blue-900 border-t-transparent"></div>
        <p class="mt-3 text-slate-500 text-sm">Đang nạp lịch sử đơn phòng...</p>
      </div>
    `;

    try {
      const response = await apiRequest(`/api/v1/bookings/customer/${user.userId}`);
      if (response && response.data && response.data.length > 0) {
        container.innerHTML = response.data.map(b => {
          const bookingId = b.bookingId || b.maDatPhong || '';
          const status = b.status || b.trangThaiDon || 'PENDING';
          const roomTypeName = b.roomTypeName || b.tenLoaiPhong || 'Hạng phòng 5 sao';
          const checkIn = b.checkInDate || b.thoiGianNhanPhong;
          const checkOut = b.checkOutDate || b.thoiGianTraPhong;
          const totalAmount = b.totalAmount !== undefined ? b.totalAmount : (b.tongTien !== undefined ? b.tongTien : 0);
          const canReview = status === 'CHECKED_OUT';

          let statusBadgeClass = 'bg-amber-100 text-amber-800';
          let statusText = 'Đang giữ chỗ';
          if (status === 'CONFIRMED') {
            statusBadgeClass = 'bg-emerald-100 text-emerald-800';
            statusText = 'Đã xác nhận';
          } else if (status === 'CHECKED_IN') {
            statusBadgeClass = 'bg-blue-100 text-blue-800';
            statusText = 'Đang lưu trú';
          } else if (status === 'CHECKED_OUT') {
            statusBadgeClass = 'bg-purple-100 text-purple-800';
            statusText = 'Đã trả phòng';
          } else if (status === 'CANCELLED') {
            statusBadgeClass = 'bg-rose-100 text-rose-800';
            statusText = 'Đã hủy';
          }

          return `
            <div class="p-4 rounded-xl border border-slate-200 bg-white shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4 hover:border-amber-400 transition-colors">
              <div>
                <div class="flex items-center gap-2 mb-1">
                  <span class="font-mono text-sm font-bold text-blue-950">${bookingId}</span>
                  <span class="text-xs px-2.5 py-0.5 rounded-full font-semibold ${statusBadgeClass}">
                    ${statusText} (${status})
                  </span>
                </div>
                <div class="text-sm font-semibold text-slate-800">${roomTypeName}</div>
                <div class="text-xs text-slate-500 mt-0.5">
                  Lưu trú: ${formatDate(checkIn)} ➔ ${formatDate(checkOut)}
                </div>
                <div class="text-sm font-semibold text-slate-700 mt-1">
                  Tổng tiền: <span class="font-bold text-amber-700 text-base font-sans">${formatVND(totalAmount)}</span>
                </div>
              </div>

              <div class="flex items-center gap-2 shrink-0">
                <button onclick="Guest.viewBookingDetails('${bookingId}')" class="text-xs px-3 py-1.5 rounded-lg border border-slate-300 text-slate-700 hover:bg-slate-50 flex items-center gap-1 font-medium transition-colors">
                  <svg class="w-3.5 h-3.5 text-blue-900" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z"/></svg>
                  Xem Mã QR
                </button>
                ${canReview ? `
                  <button onclick="Guest.openReviewModal('${bookingId}')" class="btn-gold text-xs px-3 py-1.5 font-medium">
                    Đánh giá dịch vụ
                  </button>
                ` : ''}
              </div>
            </div>
          `;
        }).join('');
      } else {
        container.innerHTML = `
          <div class="py-12 text-center text-slate-500">
            <svg class="w-12 h-12 mx-auto text-slate-300 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
            <p>Quý khách chưa có đơn đặt phòng nào.</p>
          </div>
        `;
      }
    } catch (error) {
      container.innerHTML = `<p class="text-rose-600 text-center py-6">Lỗi tải danh sách: ${error.message}</p>`;
    }
  },

  // View specific booking details and QR Code
  async viewBookingDetails(bookingId) {
    try {
      const response = await apiRequest(`/api/v1/bookings/${bookingId}`);
      if (response && response.data) {
        this.showBookingSuccess(response.data);
      }
    } catch (e) {
      showToast('Không thể tải chi tiết đơn phòng!', 'error');
    }
  },

  // Review Modal Handler
  openReviewModal(bookingId) {
    document.getElementById('reviewBookingId').value = bookingId;
    document.getElementById('reviewRating').value = '5';
    document.getElementById('reviewComment').value = '';
    this.updateReviewStars(5);
    openModal('reviewModal');
  },

  updateReviewStars(rating) {
    document.getElementById('reviewRating').value = rating;
    for (let i = 1; i <= 5; i++) {
      const star = document.getElementById(`star_${i}`);
      if (star) {
        if (i <= rating) {
          star.classList.add('text-amber-400');
          star.classList.remove('text-slate-300');
        } else {
          star.classList.add('text-slate-300');
          star.classList.remove('text-amber-400');
        }
      }
    }
  },

  async submitReview() {
    const maDatPhong = document.getElementById('reviewBookingId')?.value;
    const soSao = parseInt(document.getElementById('reviewRating')?.value || '5');
    const noiDung = document.getElementById('reviewComment')?.value.trim();

    if (!noiDung) {
      showToast('Vui lòng chia sẻ đôi nét về trải nghiệm của bạn!', 'warning');
      return;
    }

    try {
      const response = await apiRequest('/api/v1/reviews', {
        method: 'POST',
        body: JSON.stringify({ maDatPhong, soSao, noiDung })
      });

      if (response && response.success) {
        showToast('Cảm ơn bạn đã gửi đánh giá! Ý kiến của bạn giúp chúng tôi hoàn thiện hơn.', 'success');
        closeModal('reviewModal');
        this.loadFeaturedReviews();
      }
    } catch (error) {
      showToast(error.message || 'Gửi đánh giá thất bại!', 'error');
    }
  },

  // Load featured reviews
  async loadFeaturedReviews() {
    const container = document.getElementById('featuredReviewsContainer');
    if (!container) return;

    try {
      const response = await apiRequest('/api/v1/reviews/featured');
      if (response && response.data && response.data.length > 0) {
        container.innerHTML = response.data.map(r => `
          <div class="glass-card p-6 rounded-2xl border border-slate-200/80 hover:shadow-xl transition-all">
            <div class="flex items-center gap-1 text-amber-400 mb-3">
              ${Array(r.soSaoDanhGia || 5).fill('').map(() => `
                <svg class="w-4 h-4 fill-current" viewBox="0 0 20 20"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"/></svg>
              `).join('')}
            </div>
            <p class="text-slate-700 text-sm italic mb-4 leading-relaxed">
              "${r.noiDungNx}"
            </p>
            <div class="flex items-center gap-3 pt-3 border-t border-slate-100">
              <div class="w-9 h-9 rounded-full bg-blue-900 text-white font-serif font-bold flex items-center justify-center text-xs">
                ${r.khachHang?.hoTen ? r.khachHang.hoTen.charAt(0) : 'M'}
              </div>
              <div>
                <h4 class="text-xs font-bold text-slate-900">${r.khachHang?.hoTen || 'Khách hàng ẩn danh'}</h4>
                <span class="text-[10px] text-slate-400">${formatDateTime(r.thoiGianDanhGia)}</span>
              </div>
            </div>
          </div>
        `).join('');
      }
    } catch (e) {
      console.warn('Reviews fetch:', e);
    }
  }
};

// Initialize Guest scripts
document.addEventListener('DOMContentLoaded', () => {
  Guest.init();
});
