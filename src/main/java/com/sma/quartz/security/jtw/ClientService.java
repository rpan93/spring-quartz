package com.sma.quartz.security.jtw;

import com.sma.common.tools.exceptions.ItemNotFoundBusinessException;
import com.sma.quartz.security.jtw.model.Client;
import com.sma.quartz.security.jtw.model.Secret;
import com.sma.quartz.security.jtw.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Transactional
public class ClientService {

    private ClientRepository clientRepository;

    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public Client getClient(String clientId, String clientSecret) {
        try {
            if (!clientId.toLowerCase().equals(UUID.fromString(clientId).toString())) {
                throw new ItemNotFoundBusinessException(String.format("Client id '%s' is not well formatted.", clientId), 404);
            }
        } catch (IllegalArgumentException e) {
            throw new ItemNotFoundBusinessException(String.format("Client id '%s' is not well formatted.", clientId), 405);
        }

        return clientRepository.findByIdWorkaround(clientId).filter(client -> new Secret(clientSecret)
                .isMatched(client.getPasswordHash())).orElseThrow(() -> new ItemNotFoundBusinessException("Client id and client secret do not match.", 406));
    }
}
