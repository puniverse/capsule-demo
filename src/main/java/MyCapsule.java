
import java.nio.file.Path;

public class MyCapsule extends Capsule {

    public MyCapsule(Path jarFile) {
        super(jarFile);
        System.out.println("YAY!");
    }

}
