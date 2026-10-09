(() => {
    const formularioSelector = 'form[data-feedback-envio]';
    const statusSelector = '[data-feedback-envio-status]';
    const mensagem = 'Envio iniciado. Aguarde a resposta da página.';

    const limparStatus = () => {
        document.querySelectorAll(formularioSelector).forEach((formulario) => {
            const status = formulario.querySelector(statusSelector);
            if (!status) return;

            status.textContent = '';
            status.hidden = true;
        });
    };

    document.querySelectorAll(formularioSelector).forEach((formulario) => {
        const status = formulario.querySelector(statusSelector);
        if (!status) return;

        formulario.addEventListener('submit', (evento) => {
            status.textContent = mensagem;
            status.hidden = false;

            Promise.resolve().then(() => {
                if (!evento.defaultPrevented) return;

                status.textContent = '';
                status.hidden = true;
            });
        });
    });

    window.addEventListener('pageshow', limparStatus);
})();
