/**
 * MELIA HOTEL - Housekeeping Management Module
 * Room status Kanban/grid board by floor, status toggle, and Minibar usage reporting.
 */

const Housekeeping = {
  allRooms: [],
  currentFilter: '',

  init() {
    this.loadRooms();
  },

  async loadRooms(status = '') {
    this.currentFilter = status;
    const container = document.getElementById('housekeepingRoomsGrid');
    if (!container) return;

    container.innerHTML = `
      <div class="col-span-full py-16 text-center">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-amber-600 border-t-transparent"></div>
        <p class="mt-3 text-slate-500 text-sm">Đang tải sơ đồ trạng thái phòng...</p>
      </div>
    `;

    try {
      const endpoint = status ? `/api/v1/housekeeping/rooms?status=${status}` : '/api/v1/housekeeping/rooms';
      const response = await apiRequest(endpoint);
      if (response && response.data) {
        this.allRooms = response.data;
        this.renderRoomsBoard(response.data);
      }
    } catch (error) {
      container.innerHTML = `<p class="text-rose-600 text-center py-8">Lỗi tải danh sách phòng: ${error.message}</p>`;
    }
  },

  renderRoomsBoard(rooms) {
    const container = document.getElementById('housekeepingRoomsGrid');
    if (!container) return;

    if (rooms.length === 0) {
      container.innerHTML = `
        <div class="col-span-full py-12 text-center text-slate-400">
          <p>Không có phòng nào phù hợp với bộ lọc hiện tại.</p>
        </div>
      `;
      return;
    }

    // Group rooms by floor
    const floors = {};
    rooms.forEach(r => {
      const fl = r.tang || 1;
      if (!floors[fl]) floors[fl] = [];
      floors[fl].push(r);
    });

    let html = '';
    const sortedFloors = Object.keys(floors).sort((a, b) => a - b);

    sortedFloors.forEach(floorNum => {
      html += `
        <div class="col-span-full mb-2 mt-4 first:mt-0">
          <div class="flex items-center gap-3">
            <h4 class="font-bold text-sm text-slate-700 uppercase tracking-wider flex items-center gap-2">
              <span class="w-2.5 h-2.5 rounded-full bg-blue-900"></span>
              Tầng ${floorNum}
            </h4>
            <div class="flex-1 border-t border-slate-200"></div>
            <span class="text-xs text-slate-400 font-medium">${floors[floorNum].length} phòng</span>
          </div>
        </div>
      `;

      floors[floorNum].forEach(r => {
        const statusClass = 
          r.trangThaiPhong === 'AVAILABLE' ? 'status-available' :
          r.trangThaiPhong === 'OCCUPIED' ? 'status-occupied' :
          r.trangThaiPhong === 'DIRTY' ? 'status-dirty' : 'status-maintenance';

        const statusLabel =
          r.trangThaiPhong === 'AVAILABLE' ? 'Sạch (Sẵn sàng)' :
          r.trangThaiPhong === 'OCCUPIED' ? 'Đang có khách' :
          r.trangThaiPhong === 'DIRTY' ? 'Chưa dọn (Bẩn)' : 'Bảo trì';

        html += `
          <div class="p-5 rounded-2xl bg-white border border-slate-200 shadow-sm hover:shadow-md transition-all flex flex-col justify-between">
            <div>
              <div class="flex items-center justify-between mb-3">
                <span class="font-mono text-2xl font-black text-slate-900">P.${r.soPhong}</span>
                <span class="text-xs px-2.5 py-1 rounded-full font-bold uppercase tracking-wider ${statusClass}">
                  ${statusLabel}
                </span>
              </div>
              <div class="text-xs font-semibold text-slate-700">${r.loaiPhong?.tenLoaiPhong || 'Phòng tiêu chuẩn'}</div>
              <div class="text-[11px] text-slate-400 mt-1">Mã phòng: ${r.maPhong}</div>
            </div>

            <!-- Housekeeping Quick Actions -->
            <div class="mt-5 pt-3 border-t border-slate-100 flex flex-wrap gap-1.5">
              ${r.trangThaiPhong === 'DIRTY' ? `
                <button onclick="Housekeeping.changeStatus('${r.maPhong}', 'AVAILABLE')" class="w-full text-xs font-semibold px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg shadow-sm transition-all flex items-center justify-center gap-1">
                  <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>
                  Đã dọn xong ➔ Sẵn sàng
                </button>
              ` : ''}

              ${r.trangThaiPhong === 'AVAILABLE' ? `
                <button onclick="Housekeeping.changeStatus('${r.maPhong}', 'DIRTY')" class="text-xs px-2.5 py-1 bg-amber-50 hover:bg-amber-100 text-amber-800 rounded-lg border border-amber-200 transition-colors">
                  Đánh dấu bẩn
                </button>
                <button onclick="Housekeeping.changeStatus('${r.maPhong}', 'MAINTENANCE')" class="text-xs px-2.5 py-1 bg-rose-50 hover:bg-rose-100 text-rose-800 rounded-lg border border-rose-200 transition-colors">
                  Bảo trì
                </button>
              ` : ''}

              ${r.trangThaiPhong === 'MAINTENANCE' ? `
                <button onclick="Housekeeping.changeStatus('${r.maPhong}', 'AVAILABLE')" class="w-full text-xs font-semibold px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-sm transition-all">
                  Hoàn tất bảo trì ➔ Sẵn sàng
                </button>
              ` : ''}

              <button onclick="Housekeeping.openMinibarModal('${r.maPhong}', '${r.soPhong}')" class="w-full mt-1 text-xs px-2.5 py-1 text-slate-600 hover:text-blue-900 hover:bg-slate-50 rounded-lg border border-slate-200 transition-colors flex items-center justify-center gap-1">
                <svg class="w-3.5 h-3.5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"/></svg>
                Ghi nhận Minibar
              </button>
            </div>
          </div>
        `;
      });
    });

    container.innerHTML = html;
  },

  async changeStatus(roomId, newStatus) {
    try {
      const response = await apiRequest(`/api/v1/housekeeping/rooms/${roomId}/status`, {
        method: 'PUT',
        body: JSON.stringify({ status: newStatus })
      });

      if (response && response.success) {
        showToast(`Cập nhật trạng thái phòng thành công: ${newStatus}`, 'success');
        this.loadRooms(this.currentFilter);
      }
    } catch (error) {
      showToast(error.message || 'Cập nhật thất bại!', 'error');
    }
  },

  openMinibarModal(roomId, roomNumber) {
    document.getElementById('minibarRoomId').value = roomId;
    document.getElementById('minibarRoomNumber').innerText = roomNumber;
    document.getElementById('minibarBookingId').value = '';
    document.getElementById('minibarQuantity').value = '1';
    openModal('minibarModal');
  },

  async submitMinibarUsage() {
    const maDatPhong = document.getElementById('minibarBookingId')?.value.trim();
    const maDichVu = document.getElementById('minibarServiceSelect')?.value;
    const soLuong = parseInt(document.getElementById('minibarQuantity')?.value || '1');

    if (!maDatPhong) {
      showToast('Vui lòng nhập Mã đặt phòng của khách đang lưu trú tại phòng này!', 'warning');
      return;
    }

    try {
      const response = await apiRequest('/api/v1/housekeeping/minibar-usage', {
        method: 'POST',
        body: JSON.stringify({ maDatPhong, maDichVu, soLuong })
      });

      if (response && response.success) {
        showToast(`Ghi nhận tiêu thụ minibar thành công cho đơn ${maDatPhong}!`, 'success');
        closeModal('minibarModal');
      }
    } catch (error) {
      showToast(error.message || 'Ghi nhận thất bại!', 'error');
    }
  }
};
