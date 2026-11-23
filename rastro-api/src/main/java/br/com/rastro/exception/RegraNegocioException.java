package br.com.rastro.exception;

public class RegraNegocioException extends RuntimeException{
    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
