package za.co.pp.data.mapper;

import java.io.IOException;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MultipartFileMapper {

    @Named("multipartFleToByteArray")
    public byte[] multipartFleToByteArray(MultipartFile multipartFile) throws IOException {
        return multipartFile.getBytes();
    }

}
