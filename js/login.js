(function (window, document) {
  const app = window.GestorEstoque;

  function normalize(value) {
    return String(value || '').trim().toLowerCase();
  }

  function findAuthenticatedUser(usuarioValor, senhaValor) {
    if (usuarioValor === normalize(app.usuario.usuario) && senhaValor === String(app.usuario.senha || '').trim()) {
      return {
        id: 'fixed-admin',
        nome: app.usuario.nome,
        perfil: 'Admin',
        usuario: app.usuario.usuario,
      };
    }

    return app.state.usuarios.find((user) => {
      const loginName = normalize(user.usuario || user.nome);
      return loginName === usuarioValor && String(user.senha || '').trim() === senhaValor;
    }) || null;
  }

  function showLoginError(message) {
    const errorBox = document.getElementById('errorMessage');
    if (!errorBox) {
      return;
    }

    errorBox.textContent = message;
    errorBox.classList.add('show');
  }

  function hideLoginError() {
    const errorBox = document.getElementById('errorMessage');
    if (!errorBox) {
      return;
    }

    errorBox.textContent = '';
    errorBox.classList.remove('show');
  }

  function handleLogin(event) {
    event.preventDefault();

    const usuario = document.getElementById('usuario');
    const senha = document.getElementById('senha');

    const usuarioValor = usuario.value.trim();
    const senhaValor = senha.value.trim();
    const usuarioKey = normalize(usuarioValor);

    if (!usuarioKey || !senhaValor) {
      showLoginError('Preencha usuario e senha para continuar.');
      return;
    }

    const authenticatedUser = findAuthenticatedUser(usuarioKey, senhaValor);
    if (authenticatedUser) {
      const profile = String(authenticatedUser.perfil || '').trim().toLowerCase();
      app.setSession({
        usuario: authenticatedUser.usuario || authenticatedUser.nome,
        nome: authenticatedUser.nome,
        perfil: profile,
        autenticadoEm: new Date().toISOString(),
      });

      window.location.href = `./${window.GestorEstoqueUI.getLandingRoute(profile)}`;
      return;
    }

    showLoginError('Usuario ou senha invalidos.');
  }

  function init() {
    const session = app.getSession();
    if (session) {
      window.location.href = './Menu.html';
      return;
    }

    const form = document.getElementById('loginForm');
    const usuario = document.getElementById('usuario');
    const senha = document.getElementById('senha');

    if (!form || !usuario || !senha) {
      return;
    }

    form.addEventListener('submit', handleLogin);
    usuario.addEventListener('input', hideLoginError);
    senha.addEventListener('input', hideLoginError);
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);