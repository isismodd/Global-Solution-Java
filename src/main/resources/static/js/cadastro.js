(() => {
  const form = document.getElementById('formCadastro');
  if (!form) return;
  const perfil = document.getElementById('perfil');
  const modal = document.getElementById('modalPolitica');
  const checkbox = document.getElementById('aceiteVisual');
  const aceiteHidden = document.getElementById('aceitouPolitica');
  const botaoCadastrar = document.getElementById('btnCadastrar');
  const abrir = document.getElementById('abrirPolitica');
  const fechar = document.getElementById('fecharPolitica');
  const aceitar = document.getElementById('aceitarPolitica');
  const recusar = document.getElementById('recusarPolitica');
  let focoAnterior = null;

  function abrirModal() {
    focoAnterior = document.activeElement;
    modal.classList.add('aberto');
    modal.setAttribute('aria-hidden', 'false');
    document.body.classList.add('modal-aberto');
    aceitar.focus();
  }
  function fecharModal() {
    modal.classList.remove('aberto');
    modal.setAttribute('aria-hidden', 'true');
    document.body.classList.remove('modal-aberto');
    if (focoAnterior) focoAnterior.focus();
  }
  function atualizarAceite(aceito) {
    aceiteHidden.value = aceito ? 'true' : 'false';
    checkbox.checked = aceito;
    botaoCadastrar.disabled = !aceito;
    fecharModal();
  }
  function atualizarCampos() {
    const p = perfil.value;
    document.querySelectorAll('.perfil-campo').forEach(el => el.classList.remove('visivel'));
    const pessoa = p === 'VOLUNTARIO' || p === 'BENEFICIARIO';
    document.querySelectorAll('.campo-pessoa').forEach(el => el.classList.toggle('visivel', pessoa));
    document.querySelectorAll('.campo-regiao').forEach(el => el.classList.toggle('visivel', !!p));
    document.querySelectorAll('.campo-ong').forEach(el => el.classList.toggle('visivel', p === 'ONG'));
    document.getElementById('labelNome').textContent = p === 'ONG' ? 'Nome da organização' : 'Nome completo';
    document.getElementById('nome').placeholder = p === 'ONG' ? 'Nome da ONG' : 'Digite seu nome';
    document.getElementById('cpf').required = pessoa;
    document.getElementById('dataNascimento').required = pessoa;
    document.getElementById('cnpj').required = p === 'ONG';
    document.getElementById('nomeResponsavel').required = p === 'ONG';
    document.getElementById('estado').required = !!p;
    document.getElementById('cidade').required = !!p;
  }

  perfil.addEventListener('change', atualizarCampos);
  checkbox.addEventListener('change', () => {
    if (checkbox.checked && aceiteHidden.value !== 'true') {
      checkbox.checked = false;
      abrirModal();
    } else if (!checkbox.checked) {
      atualizarAceite(false);
    }
  });
  abrir.addEventListener('click', abrirModal);
  fechar.addEventListener('click', () => atualizarAceite(false));
  recusar.addEventListener('click', () => atualizarAceite(false));
  aceitar.addEventListener('click', () => atualizarAceite(true));
  modal.addEventListener('click', e => { if (e.target === modal) atualizarAceite(false); });
  document.addEventListener('keydown', e => { if (e.key === 'Escape' && modal.classList.contains('aberto')) atualizarAceite(false); });
  form.addEventListener('submit', e => {
    if (aceiteHidden.value !== 'true') {
      e.preventDefault();
      abrirModal();
      return;
    }
    if (!form.reportValidity()) e.preventDefault();
  });
  atualizarCampos();
})();
