package sdis.utils;
import java.util.concurrent.ConcurrentHashMap;
public class BlacklistManager {
    private final ConcurrentHashMap<String, Integer> intentosPorID = new ConcurrentHashMap<>();
    private final int intentosMaximos;

    public BlacklistManager(int intentosMaximos) {
        this.intentosMaximos = intentosMaximos;
    }

    public synchronized boolean isIPBaneada(String direccionIp) {
        return intentosPorID.getOrDefault(direccionIp, 0) >= intentosMaximos;
    }

    public synchronized void registraIntento(String direccionIp) {
        intentosPorID.put(direccionIp, intentosPorID.getOrDefault(direccionIp, 0) + 1);
    }

    public Integer getIntentos(String direccionIp) {
        return intentosPorID.getOrDefault(direccionIp,0);
    }
}
