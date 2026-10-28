/**
 * MELIA HOTEL - Centralized API & Utility Module
 * Handles HTTP requests, JWT token attachment, Toast notifications, and formatting.
 */

// Dynamic API base URL: respects custom override or detects current host:8080
const API_BASE = window.API_BASE 
  || localStorage.getItem('melia_api_base') 
  || (window.location.protocol.startsWith('http') ? `${window.location.protocol}//${window.location.hostname || 'localhost'}:8080` : 'http://localhost:8080');

// Global HTTP request handler
async function apiRequest(endpoint, options = {}) {
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE}${endpoint}`;
  
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    ...(options.headers || {})
  };

  const token = localStorage.getItem('melia_token');
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(url, {
      ...options,
      headers
    });

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const errorMessage = data?.message || `Lỗi máy chủ (${response.status})`;
      throw new Error(errorMessage);
    }

    return data;
  } catch (error) {
    console.error(`API Error [${endpoint}]:`, error);
    throw error;
  }
}

// Format number to Vietnamese Dong currency (e.g. 2.500.000 ₫)
function formatVND(amount) {
  if (amount === null || amount === undefined) return '0 ₫';
  const num = typeof amount === 'number' ? amount : parseFloat(amount);
  if (isNaN(num)) return '0 ₫';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(num);
}

// Format Date object to YYYY-MM-DD (local calendar date, avoiding UTC rollback bug)
function formatLocalDate(d = new Date()) {
  if (!d) return '';
  const dateObj = (d instanceof Date) ? d : new Date(d);
  if (isNaN(dateObj.getTime())) return '';
  const year = dateObj.getFullYear();
  const month = String(dateObj.getMonth() + 1).padStart(2, '0');
  const day = String(dateObj.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

// Format date string YYYY-MM-DD to DD/MM/YYYY
function formatDate(dateStr) {
  if (!dateStr) return '';
  const parts = dateStr.split('-');
  if (parts.length === 3) {
    return `${parts[2]}/${parts[1]}/${parts[0]}`;
  }
  return dateStr;
}

// Format ISO DateTime to HH:mm DD/MM/YYYY
function formatDateTime(dtStr) {
  if (!dtStr) return '';
  try {
    const d = new Date(dtStr);
    if (isNaN(d.getTime())) return dtStr;
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    return `${hours}:${minutes} ${day}/${month}/${year}`;
  } catch (e) {
    return dtStr;
  }
}

// Luxury Toast Notification System
function showToast(message, type = 'success') {
  let toastContainer = document.getElementById('toastContainer');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toastContainer';
    toastContainer.className = 'fixed bottom-5 right-5 z-50 flex flex-col gap-3 pointer-events-none';
    document.body.appendChild(toastContainer);
  }

  const toast = document.createElement('div');
  toast.className = 'pointer-events-auto flex items-center gap-3 px-5 py-3.5 rounded-xl shadow-xl transition-all duration-300 transform translate-y-4 opacity-0 text-sm font-medium border';
  
  let iconSvg = '';
  if (type === 'success') {
    toast.className += ' bg-emerald-900/90 text-white border-emerald-700/60 backdrop-blur-md';
    iconSvg = `<svg class="w-5 h-5 text-emerald-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg>`;
  } else if (type === 'error') {
    toast.className += ' bg-rose-900/90 text-white border-rose-700/60 backdrop-blur-md';
    iconSvg = `<svg class="w-5 h-5 text-rose-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>`;
  } else if (type === 'warning') {
    toast.className += ' bg-amber-900/90 text-white border-amber-700/60 backdrop-blur-md';
    iconSvg = `<svg class="w-5 h-5 text-amber-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/></svg>`;
  } else {
    toast.className += ' bg-slate-900/90 text-white border-slate-700/60 backdrop-blur-md';
    iconSvg = `<svg class="w-5 h-5 text-sky-400 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>`;
  }

  toast.innerHTML = `
    ${iconSvg}
    <span class="flex-1">${message}</span>
    <button onclick="this.parentElement.remove()" class="text-white/60 hover:text-white ml-2 transition-colors">
      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/></svg>
    </button>
  `;

  toastContainer.appendChild(toast);

  // Trigger enter animation
  requestAnimationFrame(() => {
    toast.classList.remove('translate-y-4', 'opacity-0');
    toast.classList.add('translate-y-0', 'opacity-100');
  });

  // Auto dismiss after 4 seconds
  setTimeout(() => {
    toast.classList.add('opacity-0', 'translate-y-2');
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Helpers for modal controls
function openModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.remove('hidden');
    modal.classList.add('flex');
    document.body.style.overflow = 'hidden';
  }
}

function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.classList.add('hidden');
    modal.classList.remove('flex');
    document.body.style.overflow = '';
  }
}
