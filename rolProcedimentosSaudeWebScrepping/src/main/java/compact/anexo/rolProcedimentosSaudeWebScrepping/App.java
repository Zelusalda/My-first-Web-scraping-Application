package compact.anexo.rolProcedimentosSaudeWebScrepping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class App {

    public static void main(String[] args) {
        createDownloadDirectory();
        
        acessoAoSite();
        List<String> pdfUrls = new ArrayList<>();
        pdfUrls.add("https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf"); 
        pdfUrls.add("https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos/Anexo_II_DUT_2021_RN_465.2021_RN628.2025_RN629.2025.pdf");

        List<String> downloadedFiles = new ArrayList<>();

        for (int i = 0; i < pdfUrls.size(); i++) {
            String saveDir = "downloads/anexo" + (i + 1) + ".pdf"; 
            downloadFile(pdfUrls.get(i), saveDir);
            downloadedFiles.add(saveDir); 
        }

        String zipFileName = "anexos.zip";
        zipFiles(downloadedFiles.toArray(new String[0]), zipFileName);
    }

    public static void createDownloadDirectory() {
        File directory = new File("downloads");
        if (!directory.exists()) {
            directory.mkdir(); 
            System.out.println("Diretório 'downloads' criado.");
        }
    }

    public static List<String> acessoAoSite() {
        String url = "https://www.gov.br/ans/pt-br/acesso-a-informacao/participacao-da-sociedade/atualizacao-do-rol-de-procedimentos";
        List<String> pdfUrls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println("Título da página: " + doc.title());


            for (Element link : doc.select("a[href$=.pdf]")) {
                pdfUrls.add(link.absUrl("href")); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pdfUrls; 
    }

    public static void downloadFile(String buscaURL, String saveDir) {
        try (InputStream in = new URL(buscaURL).openStream(); 
             FileOutputStream out = new FileOutputStream(saveDir)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Arquivo baixado: " + saveDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zipFiles(String[] srcFiles, String zipFile) {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String srcFile : srcFiles) {
                try (FileInputStream fis = new FileInputStream(srcFile)) {
                    ZipEntry zipEntry = new ZipEntry(new File(srcFile).getName());
                    zos.putNextEntry(zipEntry);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                }
            }
            System.out.println("Arquivos compactados em: " + zipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}