(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const state = app.state;
  const form = document.getElementById('usuarioForm');
  const tableBody = document.getElementById('usuariosBody');
  const feedback = document.getElementById('usuarioFeedback');
  const submitButton = document.getElementById('usuarioSubmitBtn');
  const cancelButton = document.getElementById('usuarioCancelBtn');
  const hiddenId = document.getElementById('usuarioId');

  let editingId = null;

  function getUserPayload() {
    return {
      nome: document.getElementById('nomeUsuario').value.trim(),
      senha: document.getElementById('senhaUsuario').value,
      perfil: document.getElementById('perfilUsuario').value.trim(),
    };
  }

  function validateUser(payload) {
    if (!payload.nome || !payload.perfil) {
      return 'Preencha nome e perfil.';
    }

    if (!editingId && !payload.senha) {
      return 'Defina uma senha para o novo usuario.';
    }

    const duplicateName = state.usuarios.find((user) => user.nome.toLowerCase() === payload.nome.toLowerCase() && user.id !== editingId);
    if (duplicateName) {
      return 'Ja existe um usuario com este nome.';
    }

    return '';
  }

  function resetForm() {
    form.reset();
    editingId = null;
    hiddenId.value = '';
    submitButton.textContent = 'Adicionar usuario';
    cancelButton.hidden = true;
  }

  function startEdit(userId) {
    const user = app.findUserById(userId);
    if (!user) {
      return;
    }

    editingId = user.id;
    hiddenId.value = user.id;
    document.getElementById('nomeUsuario').value = user.nome;
    document.getElementById('senhaUsuario').value = '';
    document.getElementById('perfilUsuario').value = user.perfil;
    submitButton.textContent = 'Salvar edicao';
    cancelButton.hidden = false;
  }

  function removeUser(userId) {
    const index = state.usuarios.findIndex((user) => user.id === userId);
    if (index === -1) {
      return;
    }

    state.usuarios.splice(index, 1);
    app.persistState();
    renderTable();
    ui.showFeedback(feedback, 'Usuario excluido!', 'success');

    if (editingId === userId) {
      resetForm();
    }
  }

  function renderTable() {
    if (!tableBody) {
      return;
    }

    const rows = state.usuarios
      .slice()
      .sort((left, right) => left.nome.localeCompare(right.nome))
      .map((user) => `
        <tr data-user-id="${user.id}">
          <td>${user.nome}</td>
          <td>${user.perfil}</td>
          <td>
            <div class="table-actions">
              <button type="button" class="btn-outline-custom table-action-btn" data-action="edit" data-id="${user.id}">Editar</button>
              <button type="button" class="btn-outline-custom table-action-btn" data-action="delete" data-id="${user.id}">Excluir</button>
            </div>
          </td>
        </tr>
      `)
      .join('');

    tableBody.innerHTML = rows || '<tr><td colspan="3">Nenhum usuario cadastrado.</td></tr>';
  }

  function handleTableClick(event) {
    const row = event.target.closest('tr[data-user-id]');
    const actionButton = event.target.closest('button[data-action]');

    if (actionButton) {
      const userId = actionButton.dataset.id;
      if (actionButton.dataset.action === 'edit') {
        startEdit(userId);
        return;
      }

      if (actionButton.dataset.action === 'delete') {
        ui.showModal({
          title: 'Excluir usuario',
          message: 'Deseja excluir este usuario?',
          confirmText: 'Excluir',
          onConfirm: () => removeUser(userId),
        });
        return;
      }
    }

    if (row) {
      startEdit(row.dataset.userId);
    }
  }

  function handleSubmit(event) {
    event.preventDefault();

    const payload = getUserPayload();
    const validationMessage = validateUser(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    if (editingId) {
      const user = app.findUserById(editingId);
      if (!user) {
        ui.showFeedback(feedback, 'Usuario nao encontrado.', 'error');
        return;
      }

      user.nome = payload.nome;
      if (payload.senha) {
        user.senha = payload.senha;
      }
      user.perfil = payload.perfil;
      app.persistState();
      renderTable();
      ui.showFeedback(feedback, 'Usuario atualizado!', 'success');
      resetForm();
      return;
    }

    state.usuarios.push({
      id: app.generateId('user'),
      nome: payload.nome,
      senha: payload.senha,
      perfil: payload.perfil,
    });

    app.persistState();
    renderTable();
    ui.showFeedback(feedback, 'Usuario cadastrado!', 'success');
    resetForm();
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    renderTable();

    if (form) {
      form.addEventListener('submit', handleSubmit);
    }

    if (tableBody) {
      tableBody.addEventListener('click', handleTableClick);
    }

    if (cancelButton) {
      cancelButton.addEventListener('click', () => {
        resetForm();
        ui.showFeedback(feedback, 'Edicao cancelada.', 'info');
      });
    }
  }

  document.addEventListener('DOMContentLoaded', init);
})(window, document);