// Lê o erro que o GlobalExceptionHandler da API devolve (ApiErrorResponse):
// { status, message, validationErrors: { campo: "mensagem" } }
export function extrairErroApi(error, mensagemPadrao = 'Não foi possível salvar. Tente novamente.') {
    const dados = error?.response?.data
    if (!error?.response) {
        return { mensagem: 'Sem resposta da API. Verifique se o back-end está no ar.', campos: {} }
    }
    return {
        mensagem: dados?.message || mensagemPadrao,
        campos: dados?.validationErrors || {},
    }
}
