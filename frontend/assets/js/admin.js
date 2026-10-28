/**
 * MELIA HOTEL - Admin Analytics & Room Type Management Module
 * Charts with Chart.js: Occupancy Rate, Revenue & RevPAR, and Room Type Administration.
 */

const Admin = {
  occupancyChartInstance: null,
  revenueChartInstance: null,

  init() {
    // Default date range: current month using local calendar date
    const now = new Date();
    const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);

    const fromInput = document.getElementById('reportFromDate');
    const toInput = document.getElementById('reportToDate');

    if (fromInput && toInput) {
      fromInput.value = formatLocalDate(firstDay);
      toInput.value = formatLocalDate(lastDay);
    }

    this.loadOccupancyReport();
    this.loadRevenueReport();
    this.loadAdminRoomTypes();
  },

  async loadReports() {
    await Promise.all([
      this.loadOccupancyReport(),
      this.loadRevenueReport()
    ]);
  },

  // Occupancy Rate Report
  async loadOccupancyReport() {
    const fromDate = document.getElementById('reportFromDate')?.value;
    const toDate = document.getElementById('reportToDate')?.value;

    try {
      const response = await apiRequest(`/api/v1/admin/reports/occupancy?fromDate=${fromDate}&toDate=${toDate}`);
      if (response && response.data) {
        const data = response.data;
        const rate = (data.occupancyRate || 0).toFixed(1);

        document.getElementById('statOccupancyRate').innerText = `${rate}%`;
        document.getElementById('statTotalRooms').innerText = `${data.totalRooms || 0} phòng-đêm`;
        document.getElementById('statOccupiedRooms').innerText = `${data.occupiedRooms || 0} phòng-đêm`;

        this.renderOccupancyChart(data.occupiedRooms || 0, Math.max(0, (data.totalRooms || 0) - (data.occupiedRooms || 0)));
      }
    } catch (error) {
      console.warn('Occupancy error:', error);
    }
  },

  // Revenue & RevPAR Report
  async loadRevenueReport() {
    const fromDate = document.getElementById('reportFromDate')?.value;
    const toDate = document.getElementById('reportToDate')?.value;

    try {
      const response = await apiRequest(`/api/v1/admin/reports/revenue?fromDate=${fromDate}&toDate=${toDate}`);
      if (response && response.data) {
        const data = response.data;

        document.getElementById('statTotalRevenue').innerText = formatVND(data.totalRevenue || 0);
        document.getElementById('statRoomRevenue').innerText = formatVND(data.roomRevenue || 0);
        document.getElementById('statServiceRevenue').innerText = formatVND(data.serviceRevenue || 0);
        document.getElementById('statRevPAR').innerText = formatVND(data.revPar || 0);
        document.getElementById('statTotalInvoices').innerText = `${data.totalInvoices || 0} hóa đơn`;

        this.renderRevenueChart(data);
      }
    } catch (error) {
      console.warn('Revenue error:', error);
    }
  },

  renderOccupancyChart(occupied, available) {
    const canvas = document.getElementById('occupancyChart');
    if (!canvas || typeof Chart === 'undefined') return;

    if (this.occupancyChartInstance) {
      this.occupancyChartInstance.destroy();
    }

    const ctx = canvas.getContext('2d');
    this.occupancyChartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: ['Phòng Đã Bán', 'Phòng Khả Dụng'],
        datasets: [{
          data: [occupied, available],
          backgroundColor: ['#1E3A8A', '#E2E8F0'],
          borderWidth: 0,
          hoverOffset: 4
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { boxWidth: 12, font: { size: 12, family: 'Inter' } }
          }
        },
        cutout: '72%'
      }
    });
  },

  renderRevenueChart(data) {
    const canvas = document.getElementById('revenueChart');
    if (!canvas || typeof Chart === 'undefined') return;

    if (this.revenueChartInstance) {
      this.revenueChartInstance.destroy();
    }

    const ctx = canvas.getContext('2d');
    this.revenueChartInstance = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: ['Tiền Phòng', 'Dịch Vụ & Minibar', 'Tổng Doanh Thu'],
        datasets: [{
          label: 'Doanh thu (VNĐ)',
          data: [data.roomRevenue || 0, data.serviceRevenue || 0, data.totalRevenue || 0],
          backgroundColor: ['#1E3A8A', '#D4AF37', '#059669'],
          borderRadius: 8,
          barThickness: 36
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: (value) => value.toLocaleString('vi-VN') + ' đ',
              font: { family: 'Inter', size: 11 }
            },
            grid: { color: '#F1F5F9' }
          },
          x: {
            grid: { display: false },
            ticks: { font: { family: 'Inter', size: 12 } }
          }
        },
        plugins: {
          legend: { display: false }
        }
      }
    });
  },

  // Load Room Types for Admin Management
  async loadAdminRoomTypes() {
    const container = document.getElementById('adminRoomTypesTable');
    if (!container) return;

    try {
      const response = await apiRequest('/api/v1/rooms/types');
      if (response && response.data) {
        container.innerHTML = response.data.map(t => `
          <tr class="hover:bg-slate-50/80 transition-colors border-b border-slate-100">
            <td class="px-6 py-4 font-mono font-bold text-xs text-blue-900">${t.maLoaiPhong}</td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <img src="${t.hinhAnhMinhHoa || 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=100'}" class="w-12 h-10 object-cover rounded-lg">
                <span class="font-bold text-sm text-slate-800">${t.tenLoaiPhong}</span>
              </div>
            </td>
            <td class="px-6 py-4 font-serif font-bold text-sm text-slate-900">${formatVND(t.giaPhongCoBan)}</td>
            <td class="px-6 py-4 text-xs font-semibold text-slate-600">${t.sucChua} khách</td>
            <td class="px-6 py-4 text-xs text-slate-500 max-w-xs truncate">${t.thongTin || t.moTa || 'N/A'}</td>
          </tr>
        `).join('');
      }
    } catch (e) {
      container.innerHTML = `<tr><td colspan="5" class="py-4 text-center text-rose-500">Lỗi tải danh mục: ${e.message}</td></tr>`;
    }
  },

  // Create new room type
  async createRoomType() {
    const maLoaiPhong = document.getElementById('newRoomTypeCode')?.value.trim();
    const tenLoaiPhong = document.getElementById('newRoomTypeName')?.value.trim();
    const giaPhongCoBan = parseFloat(document.getElementById('newRoomTypePrice')?.value || '0');
    const sucChua = parseInt(document.getElementById('newRoomTypeCapacity')?.value || '2');
    const hinhAnhMinhHoa = document.getElementById('newRoomTypeImage')?.value.trim();
    const moTa = document.getElementById('newRoomTypeDescription')?.value.trim();

    if (!maLoaiPhong || !tenLoaiPhong || !giaPhongCoBan) {
      showToast('Vui lòng điền đầy đủ các thông tin bắt buộc!', 'warning');
      return;
    }

    try {
      const response = await apiRequest('/api/v1/rooms/types', {
        method: 'POST',
        body: JSON.stringify({
          maLoaiPhong,
          tenLoaiPhong,
          giaPhongCoBan,
          sucChua,
          thongTin: moTa,
          hinhAnhMinhHoa: hinhAnhMinhHoa || 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800&q=80',
          moTa
        })
      });

      if (response && response.success) {
        showToast(`Thêm hạng phòng [${tenLoaiPhong}] thành công!`, 'success');
        closeModal('createRoomTypeModal');
        this.loadAdminRoomTypes();
      }
    } catch (error) {
      showToast(error.message || 'Thêm hạng phòng thất bại!', 'error');
    }
  }
};
