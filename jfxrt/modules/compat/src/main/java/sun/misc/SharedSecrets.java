package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public class SharedSecrets {

    public static JavaSecurityAccess getJavaSecurityAccess() {
        return new JavaSecurityAccessImpl();
    }

}
