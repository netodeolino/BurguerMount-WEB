package com.hamburgueria.util;

import java.io.IOException;

import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class Image {

	public static String imagemBase64(MultipartFile imagem) throws IOException {
		StringBuilder imagem64 = new StringBuilder();
		imagem64.append("data:image/png;base64,");
		imagem64.append(StringUtils.newStringUtf8(Base64.encodeBase64(imagem.getBytes(), false)));
		return imagem64.toString();
	}
}
