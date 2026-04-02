// ================================================
// Gestor de Estoque V2 - Main JS
// ================================================

document.addEventListener('DOMContentLoaded', () => {

  // ── Sidebar toggle (dashboard) ──────────────────
  const sidebar        = document.getElementById('sidebar');
  const overlay        = document.getElementById('sidebarOverlay');
  const toggleBtn      = document.getElementById('sidebarToggle');

  function openSidebar() {
    sidebar?.classList.add('open');
    overlay?.classList.add('show');
    document.body.style.overflow = 'hidden';
  }

  function closeSidebar() {
    sidebar?.classList.remove('open');
    overlay?.classList.remove('show');
    document.body.style.overflow = '';
  }

  toggleBtn?.addEventListener('click', () => {
    sidebar?.classList.contains('open') ? closeSidebar() : openSidebar();
  });

  overlay?.addEventListener('click', closeSidebar);

  // Close sidebar on resize to desktop
  window.addEventListener('resize', () => {
    if (window.innerWidth > 768) closeSidebar();
  });

  // ── Active nav item ─────────────────────────────
  const navItems = document.querySelectorAll('.nav-item');
  navItems.forEach(item => {
    item.addEventListener('click', () => {
      navItems.forEach(n => n.classList.remove('active'));
      item.classList.add('active');
      // Auto-close sidebar on mobile after click
      if (window.innerWidth <= 768) closeSidebar();
    });
  });

  // ── Login form validation ───────────────────────
  const loginForm  = document.getElementById('loginForm');
  const loginError = document.getElementById('loginError');

  loginForm?.addEventListener('submit', (e) => {
    e.preventDefault();
    const user = loginForm.querySelector('#username').value.trim();
    const pass = loginForm.querySelector('#password').value.trim();

    loginError?.classList.remove('show');

    if (!user || !pass) {
      showError('Por favor, preencha todos os campos.');
      return;
    }

    // Demo: accept any non-empty credentials → go to dashboard
    // In a real app, replace with an API call
    const btn = loginForm.querySelector('.login-submit');
    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Entrando…';

    setTimeout(() => {
      window.location.href = 'dashboard.html';
    }, 900);
  });

  function showError(msg) {
    if (!loginError) return;
    loginError.querySelector('.err-msg').textContent = msg;
    loginError.classList.add('show');
    loginError.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
  }

  // ── Animate stat bars on dashboard ─────────────
  const bars = document.querySelectorAll('.mini-chart .mc-bar, .bar');
  bars.forEach((bar, i) => {
    bar.style.animationDelay = `${i * 0.08}s`;
  });

  // ── Tooltips (Bootstrap) ────────────────────────
  if (typeof bootstrap !== 'undefined') {
    const tooltipEls = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipEls.forEach(el => new bootstrap.Tooltip(el));
  }

});
