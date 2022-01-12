package chatapp.client.interfaces;

import chatapp.shared.models.User;

public interface DownloadListener {
    void acceptDownload(User user, byte[] hash, byte[] connection);
}
