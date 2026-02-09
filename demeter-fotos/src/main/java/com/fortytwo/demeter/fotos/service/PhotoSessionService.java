package com.fortytwo.demeter.fotos.service;

import com.fortytwo.demeter.common.dto.PagedResponse;
import com.fortytwo.demeter.common.exception.EntityNotFoundException;
import com.fortytwo.demeter.fotos.dto.CreatePhotoSessionRequest;
import com.fortytwo.demeter.fotos.dto.EstimationDTO;
import com.fortytwo.demeter.fotos.dto.PhotoSessionDTO;
import com.fortytwo.demeter.fotos.dto.SessionStatusDTO;
import com.fortytwo.demeter.fotos.model.PhotoProcessingSession;
import com.fortytwo.demeter.fotos.model.ProcessingStatus;
import com.fortytwo.demeter.fotos.repository.EstimationRepository;
import com.fortytwo.demeter.fotos.repository.PhotoProcessingSessionRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PhotoSessionService {

    private static final Logger log = Logger.getLogger(PhotoSessionService.class);

    @Inject
    PhotoProcessingSessionRepository sessionRepository;

    @Inject
    EstimationRepository estimationRepository;

    public PagedResponse<PhotoSessionDTO> findAll(int page, int size) {
        var query = sessionRepository.findAll();
        var sessions = query.page(Page.of(page, size)).list();
        long total = query.count();
        var dtos = sessions.stream().map(PhotoSessionDTO::from).toList();
        return PagedResponse.of(dtos, page, size, total);
    }

    public PhotoSessionDTO findById(UUID id) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", id));
        return PhotoSessionDTO.from(session);
    }

    public SessionStatusDTO getSessionStatus(UUID id) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", id));
        return SessionStatusDTO.from(session);
    }

    public List<EstimationDTO> findEstimationsBySessionId(UUID sessionId) {
        sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        return estimationRepository.findBySessionId(sessionId)
                .stream().map(EstimationDTO::from).toList();
    }

    @Transactional
    public PhotoSessionDTO create(CreatePhotoSessionRequest request) {
        PhotoProcessingSession session = new PhotoProcessingSession();
        session.setProductId(request.productId());
        session.setBatchId(request.batchId());
        session.setStatus(ProcessingStatus.PENDING);
        session.setTotalImages(0);
        session.setProcessedImages(0);

        sessionRepository.persist(session);
        log.infof("Created photo processing session: %s", session.getId());
        return PhotoSessionDTO.from(session);
    }

    @Transactional
    public SessionStatusDTO updateProcessingProgress(UUID sessionId, int processedCount) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        session.setProcessedImages(processedCount);

        if (session.getStatus() == ProcessingStatus.PENDING) {
            session.setStatus(ProcessingStatus.PROCESSING);
        }

        log.infof("Updated session %s progress: %d/%d", sessionId, processedCount, session.getTotalImages());
        return SessionStatusDTO.from(session);
    }

    @Transactional
    public SessionStatusDTO completeSession(UUID sessionId) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        session.setStatus(ProcessingStatus.COMPLETED);
        session.setProcessedImages(session.getTotalImages());

        log.infof("Completed photo processing session: %s", sessionId);
        return SessionStatusDTO.from(session);
    }

    @Transactional
    public SessionStatusDTO failSession(UUID sessionId) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", sessionId));
        session.setStatus(ProcessingStatus.FAILED);

        log.warnf("Failed photo processing session: %s", sessionId);
        return SessionStatusDTO.from(session);
    }

    @Transactional
    public void delete(UUID id) {
        PhotoProcessingSession session = sessionRepository.findByIdOptional(id)
                .orElseThrow(() -> new EntityNotFoundException("PhotoProcessingSession", id));
        sessionRepository.delete(session);
        log.infof("Deleted photo processing session: %s", id);
    }
}
