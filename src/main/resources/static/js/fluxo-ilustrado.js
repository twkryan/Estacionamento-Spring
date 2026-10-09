(() => {
    const secao = document.querySelector('#como-funciona');
    const botao = document.querySelector('#controle-exemplo');
    if (!secao || !botao) return;

    const etapas = [...secao.querySelectorAll('.fluxo > li[data-demo-step]')];
    if (etapas.length !== 3) return;

    const consultaMovimento = window.matchMedia('(prefers-reduced-motion: reduce)');
    const duracaoTotal = 960;
    const intervaloEtapa = duracaoTotal / etapas.length;
    const validadeSinalPonteiro = 800;
    let estado = 'idle';
    let geracao = 0;
    let temporizadores = [];
    let ponteiroPendente = null;
    let ponteiroConcluido = null;
    let temporizadorSinal = null;

    const limparTemporizadores = () => {
        temporizadores.forEach(window.clearTimeout);
        temporizadores = [];
    };

    const definirEtapa = (etapa, destacada) => {
        etapa.dataset.demoHighlighted = String(destacada);
    };

    const definirEstado = (novoEstado) => {
        estado = novoEstado;
        secao.dataset.demoState = novoEstado;
        botao.textContent = novoEstado === 'running'
            ? 'Cancelar exemplo'
            : novoEstado === 'completed' ? 'Repetir exemplo' : 'Ver exemplo';
    };

    const consumirSinalPonteiro = (evento) => {
        const sinal = ponteiroConcluido;
        ponteiroConcluido = null;
        window.clearTimeout(temporizadorSinal);
        temporizadorSinal = null;

        return Boolean(evento.isTrusted && sinal
            && evento.pointerId === sinal.pointerId
            && evento.pointerType === sinal.pointerType
            && ['mouse', 'touch', 'pen'].includes(evento.pointerType));
    };

    const limparSinaisPonteiro = () => {
        ponteiroPendente = null;
        ponteiroConcluido = null;
        window.clearTimeout(temporizadorSinal);
        temporizadorSinal = null;
    };

    const reiniciar = () => {
        geracao += 1;
        limparTemporizadores();
        secao.dataset.demoMotion = 'instant';
        etapas.forEach(etapa => definirEtapa(etapa, false));
        definirEstado('idle');
    };

    const concluirImediatamente = () => {
        geracao += 1;
        limparTemporizadores();
        secao.dataset.demoMotion = 'instant';
        etapas.forEach(etapa => definirEtapa(etapa, true));
        definirEstado('completed');
    };

    const iniciarAnimacao = () => {
        geracao += 1;
        const minhaGeracao = geracao;
        limparTemporizadores();
        etapas.forEach(etapa => definirEtapa(etapa, false));
        secao.dataset.demoMotion = 'pointer';
        definirEstado('running');
        definirEtapa(etapas[0], true);

        for (let indice = 1; indice < etapas.length; indice += 1) {
            temporizadores.push(window.setTimeout(() => {
                if (geracao === minhaGeracao && estado === 'running') {
                    definirEtapa(etapas[indice], true);
                }
            }, indice * intervaloEtapa));
        }

        temporizadores.push(window.setTimeout(() => {
            if (geracao !== minhaGeracao || estado !== 'running') return;
            secao.dataset.demoMotion = 'instant';
            definirEstado('completed');
            limparTemporizadores();
        }, duracaoTotal));
    };

    botao.addEventListener('pointerdown', evento => {
        limparSinaisPonteiro();
        if (!evento.isTrusted || !evento.isPrimary || evento.button !== 0
                || !['mouse', 'touch', 'pen'].includes(evento.pointerType)) return;
        ponteiroPendente = {
            pointerId: evento.pointerId,
            pointerType: evento.pointerType
        };
    });

    botao.addEventListener('pointerup', evento => {
        const sinal = ponteiroPendente;
        ponteiroPendente = null;
        if (!evento.isTrusted || !evento.isPrimary || evento.button !== 0 || !sinal
                || evento.pointerId !== sinal.pointerId
                || evento.pointerType !== sinal.pointerType) return;

        ponteiroConcluido = sinal;
        temporizadorSinal = window.setTimeout(limparSinaisPonteiro, validadeSinalPonteiro);
    });

    botao.addEventListener('pointercancel', limparSinaisPonteiro);
    botao.addEventListener('click', evento => {
        const ativacaoPonteiro = consumirSinalPonteiro(evento);
        ponteiroPendente = null;

        if (estado === 'running') {
            reiniciar();
            return;
        }

        if (ativacaoPonteiro && !consultaMovimento.matches) iniciarAnimacao();
        else concluirImediatamente();
    });

    const movimentoAlterado = evento => {
        if (evento.matches && estado === 'running') concluirImediatamente();
    };
    if (typeof consultaMovimento.addEventListener === 'function') {
        consultaMovimento.addEventListener('change', movimentoAlterado);
    } else {
        consultaMovimento.addListener(movimentoAlterado);
    }

    botao.hidden = false;
})();
