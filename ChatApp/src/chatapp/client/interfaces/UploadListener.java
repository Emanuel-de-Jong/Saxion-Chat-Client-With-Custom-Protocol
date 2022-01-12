package chatapp.client.interfaces;

import chatapp.shared.models.User;

public interface UploadListener {
    void requestUpload(User user, String fileName, int fileSize, byte[] hash, byte[] connection);
}
