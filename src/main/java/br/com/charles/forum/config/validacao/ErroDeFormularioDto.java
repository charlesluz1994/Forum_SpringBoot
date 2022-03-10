package br.com.charles.forum.config.validacao;

public class ErroDeFormularioDto {

	private String campo;
	private String mensagem;

	public ErroDeFormularioDto(String campo, String mensagem) {
		super();
		this.campo = campo;
		this.mensagem = mensagem;
	}

	public String getCampo() {
		return campo;
	}

	public String getMensagem() {
		return mensagem;
	}

}
