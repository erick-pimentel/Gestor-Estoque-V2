(function (window, document) {
  const app = window.GestorEstoque;
  const ROLE_ACCESS = {
    admin: ['menu.html', 'cadprodutos.html', 'cadfornecedores.html', 'movimentacoes.html', 'relatorios.html', 'gestaodeusuario.html'],
    operador: ['menu.html', 'cadprodutos.html', 'cadfornecedores.html'],
    consulta: ['menu.html', 'movimentacoes.html', 'relatorios.html'],
  };

  const ROLE_LANDING = {
    admin: 'Menu.html',
    operador: 'Menu.html',
    consulta: 'Menu.html',
  };

  const NOTICE_KEY = 'gestor-estoque-notice-v1';
  const ACCESS_DENIED_MESSAGE = 'Acesso Negado! O nivel de acesso não é suficiente';

  function currentPageName() {
    const path = window.location.pathname.split('/').pop() || '';
    return path.toLowerCase();
  }

  function normalizeRole(role) {
    return String(role || '').trim().toLowerCase();
  }

  function getSessionRole() {
    const session = app.getSession();
    return session ? normalizeRole(session.perfil) : '';
  }

  function getAllowedPages(role) {
    return ROLE_ACCESS[normalizeRole(role)] || [];
  }

  function getLandingRoute(role) {
    return ROLE_LANDING[normalizeRole(role)] || 'Login.html';
  }

  function requireSession() {
    if (app.getSession()) {
      return true;
    }

    window.location.href = './Login.html';
    return false;
  }

  function requirePageAccess() {
    const session = app.getSession();
    if (!session) {
      window.location.href = './Login.html';
      return false;
    }

    const role = normalizeRole(session.perfil);
    const allowedPages = getAllowedPages(role);
    const current = currentPageName();

    if (!allowedPages.includes(current)) {
      setTransientNotice(ACCESS_DENIED_MESSAGE, 'error');
      window.location.href = `./${getLandingRoute(role)}`;
      return false;
    }

    return true;
  }

  function setTransientNotice(message, type) {
    if (!window.sessionStorage) {
      return;
    }

    window.sessionStorage.setItem(NOTICE_KEY, JSON.stringify({
      message,
      type: type || 'info',
    }));
  }

  function consumeTransientNotice() {
    if (!window.sessionStorage) {
      return null;
    }

    const rawNotice = window.sessionStorage.getItem(NOTICE_KEY);
    if (!rawNotice) {
      return null;
    }

    window.sessionStorage.removeItem(NOTICE_KEY);

    try {
      return JSON.parse(rawNotice);
    } catch (error) {
      return null;
    }
  }

  function logout() {
    app.clearSession();
    window.location.href = './Main.html';
  }

  function setActiveNav() {
    const current = currentPageName();
    const role = getSessionRole();
    const allowedPages = getAllowedPages(role);
    document.querySelectorAll('.nav-item').forEach((link) => {
      const href = (link.getAttribute('href') || '').split('/').pop().toLowerCase();
      const isAllowed = !allowedPages.length || allowedPages.includes(href);
      link.hidden = !isAllowed;
      link.classList.toggle('active', href === current);
    });
  }

  function renderTransientNotice() {
    const notice = consumeTransientNotice();
    if (!notice || !notice.message) {
      return;
    }

    const mainElement = document.querySelector('.dashboard-main') || document.querySelector('main');
    if (!mainElement) {
      return;
    }

    const banner = document.createElement('div');
    banner.className = `feedback-banner feedback-${notice.type || 'info'}`;
    banner.setAttribute('role', 'status');
    banner.setAttribute('aria-live', 'polite');
    banner.textContent = notice.message;
    mainElement.insertBefore(banner, mainElement.firstChild);
  }

  function bindLogoutButtons() {
    document.querySelectorAll('[data-logout-link="true"]').forEach((element) => {
      if (element.dataset.logoutBound === 'true') {
        return;
      }

      element.dataset.logoutBound = 'true';
      element.addEventListener('click', (event) => {
        event.preventDefault();
        logout();
      });
    });
  }

  function showFeedback(target, message, type) {
    const element = typeof target === 'string' ? document.querySelector(target) : target;
    if (!element) {
      return;
    }

    element.hidden = false;
    element.textContent = message;
    element.className = `feedback-banner feedback-${type || 'success'}`;

    if (element._hideTimer) {
      window.clearTimeout(element._hideTimer);
    }

    element._hideTimer = window.setTimeout(() => {
      element.hidden = true;
    }, 2600);
  }

  function hideElement(element) {
    if (element) {
      element.hidden = true;
    }
  }

  function showElement(element) {
    if (element) {
      element.hidden = false;
    }
  }

  let modalState = null;

  function ensureModal() {
    let overlay = document.getElementById('appModalOverlay');
    if (overlay) {
      return overlay;
    }

    overlay = document.createElement('div');
    overlay.id = 'appModalOverlay';
    overlay.className = 'modal-overlay';
    overlay.hidden = true;
    overlay.innerHTML = [
      '<div class="modal-dialog" role="dialog" aria-modal="true" aria-labelledby="appModalTitle">',
      '  <h3 id="appModalTitle"></h3>',
      '  <p id="appModalMessage"></p>',
      '  <div class="modal-actions">',
      '    <button type="button" class="btn-outline-custom" data-modal-cancel="true">Cancelar</button>',
      '    <button type="button" class="btn-primary-custom" data-modal-confirm="true">Confirmar</button>',
      '  </div>',
      '</div>',
    ].join('');

    overlay.addEventListener('click', (event) => {
      if (event.target === overlay || event.target.dataset.modalCancel === 'true') {
        closeModal();
      }

      if (event.target.dataset.modalConfirm === 'true') {
        const callback = modalState && modalState.onConfirm;
        closeModal();
        if (typeof callback === 'function') {
          callback();
        }
      }
    });

    document.body.appendChild(overlay);
    return overlay;
  }

  function showModal(options) {
    const overlay = ensureModal();
    const title = overlay.querySelector('#appModalTitle');
    const message = overlay.querySelector('#appModalMessage');
    const confirmButton = overlay.querySelector('[data-modal-confirm="true"]');
    const cancelButton = overlay.querySelector('[data-modal-cancel="true"]');

    modalState = {
      onConfirm: options && options.onConfirm,
    };

    title.textContent = (options && options.title) || 'Confirmação';
    message.textContent = (options && options.message) || 'Deseja continuar?';
    confirmButton.textContent = (options && options.confirmText) || 'Confirmar';
    cancelButton.textContent = (options && options.cancelText) || 'Cancelar';
    overlay.hidden = false;
    document.body.classList.add('modal-open');
  }

  function closeModal() {
    const overlay = document.getElementById('appModalOverlay');
    if (overlay) {
      overlay.hidden = true;
    }

    modalState = null;
    document.body.classList.remove('modal-open');
  }

  function moneyToNumber(value) {
    const number = Number(String(value).replace(',', '.'));
    return Number.isFinite(number) ? number : 0;
  }

  function numberToInput(value) {
    return Number(value) || 0;
  }

  function maskCnpj(value) {
    return String(value || '')
      .replace(/\D/g, '')
      .slice(0, 14)
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/\.(\d{3})(\d)/, '.$1/$2')
      .replace(/(\d{4})(\d)/, '$1-$2');
  }

  function isValidEmail(value) {
    return /^\S+@\S+\.\S+$/.test(String(value || '').trim().toLowerCase());
  }

  function isValidCnpj(value) {
    return /^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/.test(String(value || '').trim());
  }

  function getProductName(productId) {
    const product = app.findProductById(productId);
    return product ? product.nome : '-';
  }

  function getSupplierName(supplierId) {
    const supplier = app.findSupplierById(supplierId);
    return supplier ? supplier.nome : '-';
  }

  function getUserName(userId) {
    const user = app.findUserById(userId);
    return user ? user.nome : '-';
  }

  window.GestorEstoqueUI = {
    requireSession,
    requirePageAccess,
    logout,
    setActiveNav,
    bindLogoutButtons,
    renderTransientNotice,
    showFeedback,
    hideElement,
    showElement,
    showModal,
    closeModal,
    moneyToNumber,
    numberToInput,
    maskCnpj,
    isValidEmail,
    isValidCnpj,
    getProductName,
    getSupplierName,
    getUserName,
    getLandingRoute,
  };

  document.addEventListener('DOMContentLoaded', () => {
    bindLogoutButtons();
  });
})(window, document);