(function () {
    const normalButton = document.getElementById('normalModeButton');
    const aiButton = document.getElementById('aiModeButton');
    const normalPanel = document.getElementById('normalSearchPanel');
    const aiPanel = document.getElementById('aiSearchPanel');
    const campaignGrid = document.getElementById('categorias');
    const normalEmpty = document.getElementById('emptyState');
    const aiResultsSection = document.getElementById('aiResultsSection');
    const aiResultsGrid = document.getElementById('aiResultsGrid');
    const aiResultsCount = document.getElementById('aiResultsCount');
    const aiSummary = document.getElementById('aiSummary');
    const aiForm = document.getElementById('aiRecommendationForm');
    const aiText = document.getElementById('aiRequestText');
    const aiSubmitButton = document.getElementById('aiSubmitButton');
    const aiFeedback = document.getElementById('aiFeedback');

    if (!aiButton || !normalButton || !aiPanel || !aiForm) return;

    const profile = aiResultsSection?.dataset.profile || '';

    function setMode(mode) {
        const useAi = mode === 'ai';
        normalButton.classList.toggle('active', !useAi);
        aiButton.classList.toggle('active', useAi);
        normalButton.setAttribute('aria-pressed', String(!useAi));
        aiButton.setAttribute('aria-pressed', String(useAi));
        normalPanel.hidden = useAi;
        aiPanel.hidden = !useAi;
        campaignGrid.hidden = useAi;
        if (normalEmpty) normalEmpty.hidden = true;
        aiResultsSection.hidden = !useAi || !aiResultsGrid.hasChildNodes();
        if (useAi) aiText.focus();
    }

    normalButton.addEventListener('click', () => setMode('normal'));
    aiButton.addEventListener('click', () => setMode('ai'));

    function showFeedback(message, type) {
        aiFeedback.textContent = message;
        aiFeedback.className = `ai-feedback ${type || ''}`;
        aiFeedback.hidden = false;
    }

    function makeElement(tag, className, text) {
        const element = document.createElement(tag);
        if (className) element.className = className;
        if (text !== undefined && text !== null) element.textContent = text;
        return element;
    }

    function makeRecommendationCard(item) {
        const campaign = item.campanha || {};
        const card = makeElement('article', 'campaign-card ai-recommendation-card');
        const visual = makeElement('div', 'campaign-visual visual-peach');
        visual.appendChild(makeElement('span', 'campaign-category', campaign.categoria || 'Campanha social'));
        const initials = (campaign.nomeOng || 'ONG').trim().split(/\s+/).slice(0, 2)
            .map(part => part.charAt(0)).join('').toUpperCase();
        visual.appendChild(makeElement('span', 'visual-initials', initials || 'ONG'));
        visual.appendChild(makeElement('span', 'visual-heart', '♥'));

        const body = makeElement('div', 'campaign-body');
        const organization = makeElement('div', 'organization-line');
        organization.appendChild(makeElement('span', 'org-avatar', initials || 'ON'));
        organization.appendChild(makeElement('span', '', campaign.nomeOng || 'Organização social'));
        organization.appendChild(makeElement('span', 'verified-mark', '✓'));
        body.appendChild(organization);
        body.appendChild(makeElement('h3', '', campaign.titulo || 'Campanha recomendada'));
        body.appendChild(makeElement('p', 'campaign-description', campaign.descricao || 'Conheça esta iniciativa social.'));

        const meta = makeElement('div', 'campaign-meta');
        meta.appendChild(makeElement('span', '', `⌖ ${campaign.local || 'Local não informado'}`));
        meta.appendChild(makeElement('span', '', `♡ ${campaign.categoria || 'Ação social'}`));
        body.appendChild(meta);

        const reason = makeElement('div', 'ai-recommendation-reason');
        reason.appendChild(makeElement('strong', '', '✦ Por que recomendamos'));
        reason.appendChild(makeElement('p', '', item.motivo || 'Esta campanha parece combinar com o que você descreveu.'));
        body.appendChild(reason);

        const footer = makeElement('div', 'campaign-footer');
        footer.appendChild(makeElement('span', 'campaign-status', 'Recomendação Gemini'));
        const form = document.createElement('form');
        form.method = 'post';
        form.action = `/campanhas/${encodeURIComponent(campaign.id)}/interesse`;
        const button = makeElement('button', 'text-action', profile === 'BENEFICIARIO' ? 'Preciso de ajuda →' : 'Quero voluntariar →');
        button.type = 'submit';
        form.appendChild(button);
        footer.appendChild(form);
        body.appendChild(footer);

        card.appendChild(visual);
        card.appendChild(body);
        return card;
    }

    aiForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const texto = aiText.value.trim();

        if (texto.length < 10) {
            showFeedback('Descreva com um pouco mais de detalhes (mínimo de 10 caracteres).', 'error');
            aiText.focus();
            return;
        }

        aiSubmitButton.disabled = true;
        aiSubmitButton.classList.add('loading');
        aiSubmitButton.textContent = 'Analisando campanhas...';
        aiFeedback.hidden = true;
        aiResultsSection.hidden = true;
        aiResultsGrid.replaceChildren();

        try {
            const response = await fetch('/api/ia/recomendar-campanhas', {
                method: 'POST',
                credentials: 'same-origin',
                headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
                body: JSON.stringify({ texto })
            });

            const data = await response.json().catch(() => ({}));
            if (!response.ok) {
                const message = data.mensagem || data.message ||
                    (response.status === 401 || response.status === 403
                        ? 'Sua sessão expirou ou seu perfil não tem permissão para usar as recomendações.'
                        : 'Não foi possível buscar recomendações agora. Tente novamente.');
                throw new Error(message);
            }

            const recommendations = Array.isArray(data.recomendacoes) ? data.recomendacoes : [];
            aiSummary.textContent = data.resumo || 'Veja as campanhas que combinam com sua descrição.';
            aiResultsCount.textContent = `${recommendations.length} ${recommendations.length === 1 ? 'sugestão' : 'sugestões'}`;

            recommendations.forEach(item => {
                if (item && item.campanha && item.campanha.id != null) {
                    aiResultsGrid.appendChild(makeRecommendationCard(item));
                }
            });

            aiResultsSection.hidden = false;
            if (aiResultsGrid.childElementCount === 0) {
                showFeedback(data.resumo || 'Não encontramos campanhas compatíveis no momento. Você pode tentar descrever sua busca de outra forma.', 'info');
            } else {
                showFeedback('Recomendações encontradas! Veja os motivos de cada sugestão abaixo.', 'success');
            }
        } catch (error) {
            showFeedback(error.message || 'Ocorreu um erro ao consultar a IA. Tente novamente.', 'error');
        } finally {
            aiSubmitButton.disabled = false;
            aiSubmitButton.classList.remove('loading');
            aiSubmitButton.innerHTML = '<span aria-hidden="true">✦</span> Encontrar campanhas';
        }
    });
})();
