package za.co.pp.data.mapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.mapstruct.Named;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Component
public class MultipartFileMapper {

    @Named("multipartFleToByteArray")
    public byte[] multipartFleToByteArray(MultipartFile multipartFile) throws IOException {
        return multipartFile.getBytes();
    }

    @Named("byteArrayToMultipartFile")
    public MultipartFile byteArrayToMultipartFile(byte[] byteImage) throws IOException, TikaException, SAXException {
        Tika tika = new Tika();
        InputStream inputStream = new ByteArrayInputStream(byteImage);
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContentHandler = new BodyContentHandler();
        ParseContext parseContext = new ParseContext();
        tika.getParser().parse(inputStream, bodyContentHandler, metadata, parseContext);
        return new MockMultipartFile("product_image", "", metadata.get("Content-Type"), inputStream);
    }

}
