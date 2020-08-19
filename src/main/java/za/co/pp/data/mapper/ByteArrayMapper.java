package za.co.pp.data.mapper;

import java.util.Base64;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class ByteArrayMapper {
    @Named("byteArrayToEncodedString")
    public String byteArrayToEncodedString(byte[] image){
        return Base64.getEncoder().encodeToString(image);
    }

}
