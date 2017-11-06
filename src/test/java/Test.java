import com.jfinal.kit.HashKit;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Created by luomhy on 2015/12/8.
 */
public class Test {
    public static void main(String... args){
        String str = "STR4";
        System.out.println(HashKit.md5(str));
        System.out.println(HashKit.md5("str4".toUpperCase()));
    }
}
