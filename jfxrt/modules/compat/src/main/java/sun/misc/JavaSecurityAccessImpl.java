package sun.misc;

import java.security.AccessControlContext;
import java.security.PrivilegedAction;

public class JavaSecurityAccessImpl implements JavaSecurityAccess {

    @Override
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> action, AccessControlContext stack, AccessControlContext context) {
        return action.run();
    }

    @Override
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> action, AccessControlContext context) {
        return action.run();
    }
    
}