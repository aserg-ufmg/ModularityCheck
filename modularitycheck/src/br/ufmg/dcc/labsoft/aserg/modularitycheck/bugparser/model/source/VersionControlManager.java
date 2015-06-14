package br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.model.source;

import java.io.File;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.ufmg.dcc.labsoft.aserg.modularitycheck.bugparser.util.FileUtils;

public abstract class VersionControlManager {

	/**
	 * URL do repositorio.
	 */
	protected String repository_url;

	/**
	 * Intervalo de tempo para o qual a analise deve ser feita.
	 */
	protected Interval time_interval;

	/**
	 * Diretorio onde o codigo fonte sera baixado.
	 */
	protected File src_path;

	protected VersionControlManager(String repository_url,
			String start_date_str, String end_date_str) {
		this.repository_url = repository_url;
		this.time_interval = getIntervalFromParams(start_date_str, end_date_str);
	}

	/**
	 * Faz o download do codigo do fonte no repositorio, e armazena no local
	 * passado como parametro (path). Codigo baixado corresponde a versao do
	 * final do periodo.
	 * 
	 * @param path
	 *            diretorio onde codigo fonte sera armazenado.
	 */
	public abstract void downloadCode(File path) throws Exception;

	/**
	 * Faz o download do log do repositorio
	 * 
	 * @return
	 */
	public abstract List<Commit> getCommits();

	public File getSrcPath() {
		return src_path;
	}

	/**
	 * Remove as classes de teste do sistema, para que elas nao entrem na
	 * analise. Todos os diretorios cujo nome comeca com 'test' (test*) serão
	 * excluídos.
	 */
	public void removeTestClasses() {
		System.out.println("Deleting test folders");
		DirectoryScanner scanner = new DirectoryScanner();
		String[] includes = { "**/test*" };
		scanner.setIncludes(includes);
		scanner.setBasedir(src_path);
		scanner.setCaseSensitive(false);
		scanner.scan();

		String[] included_dirs = scanner.getIncludedDirectories();
		for (String test_dir : included_dirs) {
			File dirToDelete = new File(src_path, test_dir);
			if (dirToDelete.exists())
				FileUtils.deleteRecursive(dirToDelete);
		}
	}

	/**
	 * Transforma duas datas textuais (ex: "2008-06-14") em um objeto Interval,
	 * que representa um intervalo de tempo.
	 */
	private Interval getIntervalFromParams(String start_date_str,
			String end_date_str) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("y-M-d");
		// DateMidnight e um DateTime onde a hora e sempre 00:00.
		DateMidnight start_date = new DateMidnight(
				formatter.parseDateTime(start_date_str));
		DateMidnight end_date = new DateMidnight(
				formatter.parseDateTime(end_date_str));
		return new Interval(start_date, end_date);
	}
}
