(() => {
    const formularioSelector = 'form[data-feedback-envio]';
    const statusSelector = '[data-feedback-envio-status]';
    const mensagem = 'Envio iniciado. Aguarde a resposta da página.';

    const limparStatus = [];

    document.querySelectorAll(formularioSelector).forEach((formulario) => {
        const status = formulario.querySelector(statusSelector);
        if (!status) return;

        let geracaoEnvio = 0;

        const limpar = () => {
            geracaoEnvio += 1;
            status.textContent = '';
            status.hidden = true;
        };

        limparStatus.push(limpar);

        formulario.addEventListener('submit', (evento) => {
            const geracaoAtual = ++geracaoEnvio;

            window.setTimeout(() => {
                if (geracaoAtual !== geracaoEnvio || evento.defaultPrevented) return;

                status.textContent = mensagem;
                status.hidden = false;
            }, 0);
        });
    });

    window.addEventListener('pageshow', () => limparStatus.forEach((limpar) => limpar()));
})();
