
import java.nio.file.Path;

public class MyCapsule extends Capsule {

    public MyCapsule(Path jarFile, Path cacheDir) {
        super(jarFile, cacheDir);
        System.out.println("YAY!");
    }

}
