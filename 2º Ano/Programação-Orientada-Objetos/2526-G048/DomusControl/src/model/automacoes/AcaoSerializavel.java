package model.automacoes;

import java.io.Serializable;
import model.dispositivos.Dispositivo;

@FunctionalInterface
public interface AcaoSerializavel extends Serializable {
    void executar(Dispositivo d);
}
