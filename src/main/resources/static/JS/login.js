(function (window, document) {
  const app = window.GestorEstoque;

  function normalize(value) {
    return String(value || '').trim().toLowerCase();
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

  async function authenticate(usuarioValor, senhaValor) {
    const response = await window.fetch('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        nomeUsuario: usuarioValor,
        senhaUsuario: senhaValor,
      }),
    });

    if (!response.ok) {
      return null;
    }

    return response.json();
  }

  async function handleLogin(event) {
    event.preventDefault();

    const usuario = document.getElementById('usuario');
    const senha = document.getElementById('senha');

    const usuarioValor = usuario.value.trim();
    const senhaValor = senha.value.trim();

    if (!usuarioValor || !senhaValor) {
      showLoginError('Preencha usuario e senha para continuar.');
      return;
    }

    try {
      const loginResponse = await authenticate(usuarioValor, senhaValor);
      if (!loginResponse || !loginResponse.token) {
        showLoginError('Usuario ou senha invalidos.');
        return;
      }

      const profile = normalize(loginResponse.perfil);
      app.setSession({
        token: loginResponse.token,
        tokenType: 'Bearer',
        userId: 0,
        usuario: loginResponse.nomeUsuario || usuarioValor,
        nome: loginResponse.nomeUsuario || usuarioValor,
        perfil: profile,
        autenticadoEm: new Date().toISOString(),
      });

      window.location.href = `./${window.GestorEstoqueUI.getLandingRoute(profile)}`;
    } catch (error) {
      showLoginError('Nao foi possivel conectar ao servidor de login.');
    }
  }

  function init() {
    const ui = window.GestorEstoqueUI;

    // Em modo mockado/desenvolvimento, NÃO redirecionar automaticamente
    // Deixar o usuário escolher fazer login mesmo se houver sessão antiga

    // DESABILITADO: if (ui.isSessionValid()) {
    //   window.location.href = './Menu.html';
    //   return;
    // }

    // Limpar qualquer sessão anterior para começar do zero
    const session = app.getSession();
    if (session) {
      app.clearSession();  // ← Sempre limpa ao entrar na página de login
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