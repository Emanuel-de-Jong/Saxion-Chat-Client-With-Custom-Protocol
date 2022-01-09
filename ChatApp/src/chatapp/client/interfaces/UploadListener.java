package chatapp.client.interfaces;

import chatapp.shared.models.User;

public interface UploadListener {
    void requestUpload(byte[] connection, byte[] hash, User user);
}
