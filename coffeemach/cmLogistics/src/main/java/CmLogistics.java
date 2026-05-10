import java.util.*;
import com.zeroc.Ice.*;

public class CmLogistics {
    public static void main(String[] args) {
        List<String> extArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "CmLogistic.cfg", extArgs)) {

        }
    }
}
