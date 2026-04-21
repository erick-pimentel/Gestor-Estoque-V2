(function (window, document) {
  const app = window.GestorEstoque;
  const ui = window.GestorEstoqueUI;

  const form = document.getElementById('usuarioForm');
  const tableBody = document.getElementById('usuariosBody');
  const feedback = document.getElementById('usuarioFeedback');
  const submitButton = document.getElementById('usuarioSubmitBtn');
  const cancelButton = document.getElementById('usuarioCancelBtn');
  const hiddenId = document.getElementById('usuarioId');

  let editingId = null;

  const PROFILE_LABEL = {
    ADMINISTRADOR: 'Administrador',
    OPERADOR_DE_ESTOQUE: 'Operador de Estoque',
    GESTOR: 'Gestor',
  };

  function getUsers() {
    return app.state.usuarios;
  }

  async function loadUsers() {
    const users = await app.listUsuarios();
    app.setCollectionState('usuarios', users);
    renderTable();
  }

  function getUserPayload() {
    return {
      nomeUsuario: document.getElementById('nomeUsuario').value.trim(),
      senhaUsuario: document.getElementById('senhaUsuario').value,
      perfilUsuario: document.getElementById('perfilUsuario').value.trim(),
      nomeCompleto: document.getElementById('nomeUsuario').value.trim(),
    };
  }

  function validateUser(payload) {
    if (!payload.nomeUsuario || !payload.perfilUsuario) {
      return 'Preencha nome e perfil.';
    }

    if (!editingId && !payload.senhaUsuario) {
      return 'Defina uma senha para o novo usuario.';
    }

    const duplicateName = getUsers().find((user) =>
      String(user.nomeUsuario || '').toLowerCase() === payload.nomeUsuario.toLowerCase() && user.id !== editingId
    );
    if (duplicateName) {
      return 'Ja existe um usuario com este nome.';
    }

    return '';
  }

  function resetForm() {
    if (!form) {
      return;
    }
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
    document.getElementById('nomeUsuario').value = user.nomeUsuario;
    document.getElementById('senhaUsuario').value = '';
    document.getElementById('perfilUsuario').value = user.perfilUsuario;
    submitButton.textContent = 'Salvar edicao';
    cancelButton.hidden = false;
  }

  async function removeUser(userId) {
    try {
      await app.deleteUsuario(userId);
      await loadUsers();
      ui.showFeedback(feedback, 'Usuario excluido!', 'success');

      if (editingId === userId) {
        resetForm();
      }
    } catch (error) {
      ui.handleApiError(error, feedback);
    }
  }

  function renderTable() {
    if (!tableBody) {
      return;
    }

    const rows = getUsers()
      .slice()
      .sort((left, right) => String(left.nomeUsuario).localeCompare(String(right.nomeUsuario)))
      .map((user) => `
        <tr data-user-id="${user.id}">
          <td>${user.nomeUsuario}</td>
          <td>${PROFILE_LABEL[user.perfilUsuario] || user.perfilUsuario}</td>
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

  async function handleSubmit(event) {
    event.preventDefault();

    const payload = getUserPayload();
    const validationMessage = validateUser(payload);
    if (validationMessage) {
      ui.showFeedback(feedback, validationMessage, 'error');
      return;
    }

    if (editingId) {
      const updatePayload = {
        nomeUsuario: payload.nomeUsuario,
        senhaUsuario: payload.senhaUsuario || '',
        perfilUsuario: payload.perfilUsuario,
        nomeCompleto: payload.nomeCompleto,
      };

      try {
        await app.updateUsuario(editingId, updatePayload);
        await loadUsers();
        ui.showFeedback(feedback, 'Usuario atualizado!', 'success');
        resetForm();
      } catch (error) {
        ui.handleApiError(error, feedback);
      }
      return;
    }

    try {
      await app.createUsuario(payload);
      await loadUsers();
      ui.showFeedback(feedback, 'Usuario cadastrado!', 'success');
      resetForm();
    } catch (error) {
      ui.handleApiError(error, feedback);
    }
  }

  function init() {
    if (!ui.requireSession() || !ui.requirePageAccess()) {
      return;
    }

    ui.setActiveNav();
    ui.bindLogoutButtons();
    ui.renderTransientNotice();
    loadUsers().catch((error) => {
      ui.handleApiError(error, feedback);
    });

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