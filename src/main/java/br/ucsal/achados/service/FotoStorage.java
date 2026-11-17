package br.ucsal.achados.service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import br.ucsal.achados.config.AchadosProperties;

/** Guarda fotos em disco com nome gerado pelo sistema; nunca usa o nome enviado pelo cliente. */
@Component
public class FotoStorage {

	private static final Pattern NOME_VALIDO = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png|webp)$");
	private static final long TAMANHO_MAXIMO = 5L * 1024 * 1024;

	private final Path diretorio;

	public FotoStorage(AchadosProperties props) {
		this.diretorio = Path.of(props.diretorioUploads()).toAbsolutePath().normalize();
	}

	public ImagemEntrada validar(MultipartFile arquivo) {
		if (arquivo.getSize() > TAMANHO_MAXIMO) {
			throw new RegraNegocioException("A foto deve ter no máximo 5 MB.");
		}
		try {
			byte[] bytes = arquivo.getBytes();
			String tipo = detectarTipo(bytes);
			if (tipo == null) {
				throw new RegraNegocioException("Envie uma imagem JPEG, PNG ou WebP.");
			}
			return new ImagemEntrada(bytes, tipo);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public String salvar(ImagemEntrada imagem) {
		String nome = UUID.randomUUID() + "." + imagem.extensao();
		try {
			Files.createDirectories(diretorio);
			Files.write(diretorio.resolve(nome), imagem.bytes());
			return nome;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/** Confirma que o nome é do formato gerado por este componente e que o arquivo existe. */
	public String confirmarExistente(String nome) {
		if (nome == null || !NOME_VALIDO.matcher(nome).matches() || !Files.isRegularFile(diretorio.resolve(nome))) {
			throw new RegraNegocioException("Foto inválida. Envie a imagem novamente.");
		}
		return nome;
	}

	public Path caminho(String nome) {
		return diretorio.resolve(confirmarExistente(nome));
	}

	public String tipoDe(String nome) {
		if (nome.endsWith(".png")) {
			return "image/png";
		}
		return nome.endsWith(".webp") ? "image/webp" : "image/jpeg";
	}

	public void remover(String nome) {
		if (nome != null && NOME_VALIDO.matcher(nome).matches()) {
			try {
				Files.deleteIfExists(diretorio.resolve(nome));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	private static String detectarTipo(byte[] b) {
		if (b.length > 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
			return "image/jpeg";
		}
		if (b.length > 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
			return "image/png";
		}
		if (b.length > 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F' && b[8] == 'W' && b[9] == 'E'
				&& b[10] == 'B' && b[11] == 'P') {
			return "image/webp";
		}
		return null;
	}
}
