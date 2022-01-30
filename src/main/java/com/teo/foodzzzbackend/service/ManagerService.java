package com.teo.foodzzzbackend.service;

import com.teo.foodzzzbackend.model.*;
import com.teo.foodzzzbackend.model.dto.ReservationEmailInfoDto;
import com.teo.foodzzzbackend.model.event.ReservationStatusChangeEvent;
import com.teo.foodzzzbackend.repository.*;
import com.teo.foodzzzbackend.security.payload.response.MessageResponse;
import com.teo.foodzzzbackend.security.service.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class ManagerService {
    private static final Logger logger = LoggerFactory.getLogger(ManagerService.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private KitchenTypeRepository kitchenTypeRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LocalTypeRepository localTypeRepository;

    @Autowired
    private DBFileRepository dbFileRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserDetailsServiceImpl service;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TableFormRepository tableFormRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private EmailService emailService;

    public void updateReservationStatus(String reservationCode, ReservationStatus reservationStatus) throws ParseException {
        Integer reservationId = Integer.parseInt(reservationCode);

        Optional<Reservation> reservation = reservationRepository.findById(reservationId);

        if (reservation.isPresent()) {
            if (reservationStatus.equals(ReservationStatus.DECLINED)) {
                reservationRepository.changeReservationStatusToDeclined(reservationId);
            } else {
                reservationRepository.changeReservationStatusToAccepted(reservationId);
            }
            ReservationEmailInfoDto reservationEmailInfoDto = new ReservationEmailInfoDto();
            Reservation updatedReservation = reservation.get();
            reservationEmailInfoDto.setReservationId(updatedReservation.getReservationId());
            reservationEmailInfoDto.setReservationDate(updatedReservation.getReservationDate());
            reservationEmailInfoDto.setReservationHour(updatedReservation.getReservationHour());
            reservationEmailInfoDto.setReservationStatus(updatedReservation.getReservationStatus());
            reservationEmailInfoDto.setReservationConfirmationStatus(updatedReservation.getReservationConfirmationStatus());
            reservationEmailInfoDto.setRestaurantName(updatedReservation.getRestaurant().getRestaurantName());
            reservationEmailInfoDto.setUsername(updatedReservation.getUser().getUsername());
            reservationEmailInfoDto.setEmail(updatedReservation.getUser().getEmail());
            eventPublisher.publishEvent(new ReservationStatusChangeEvent(this, reservationEmailInfoDto, reservationStatus));
        } else {
            logger.debug("Update reservation status - Reservation not found for id  " + reservationId);
        }
    }

    public List<KitchenType> findAllKitchenTypes() {
        return kitchenTypeRepository.findAll();
    }

    public List<LocalType> findAllLocalTypes() {
        return localTypeRepository.findAll();
    }

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    @Transactional
    public List<TableForm> saveTables(List<TableFormDTO> tables, String restaurantId,
                                      String width, String height) {
        restaurantRepository.updateRestaurantWidthAndHeight(Integer.parseInt(restaurantId), Integer.parseInt(width),
                Integer.parseInt(height));
        List<TableForm> savedTables = new ArrayList<>();
        if (tables != null && tables.size() >= 1)
            tableFormRepository.deleteByRestaurantId(tables.get(0).getRestaurantId());
        for (TableFormDTO tableForm : tables) {
            TableForm table = new TableForm();
            table.setTableNumber(tableForm.getTableNumber());
            table.setNumberOfPersons(tableForm.getNumberOfPersons());
            table.setRestaurant(restaurantService.findRestaurantById(tableForm.getRestaurantId().toString()));
            table.setPositionX(tableForm.getPositionX());
            table.setPositionY(tableForm.getPositionY());
            table.setType(tableForm.getType());
            table.setScaleX(tableForm.getScaleX());
            table.setScaleY(tableForm.getScaleY());
            table.setHeight(tableForm.getHeight());
            table.setWidth(tableForm.getWidth());
            TableForm saved = tableFormRepository.save(table);
            savedTables.add(saved);
        }
        return savedTables;
    }

    private boolean findTag(List<Tag> newtTags, Tag tag) {
        for (Tag tag1 : newtTags) {
            if (tag1.getTagName().equals(tag.getTagName()))
                return true;
        }
        return false;
    }

    private void updateTags(RestaurantUpdateDTO restaurantDTO, Restaurant rest) {
        List<Tag> oldTags = new ArrayList<>();
        List<Tag> getTags = restaurantService.findAllTagsByRestaurantId(restaurantDTO.getId().toString());
        if (getTags != null) {
            oldTags = getTags;
        }
        List<Tag> newTags = restaurantDTO.getTags();
        if (newTags != null)
            for (Tag tag : newTags) {
                tag.setRestaurant(rest);
            }

        for (Tag tag : oldTags) {
            if (!findTag(newTags, tag))
                tagRepository.delete(tag);
        }
        assert newTags != null;
        for (Tag tag : newTags) {
            tagRepository.save(tag);
        }
    }

    public User saveUserManager(User user) {
        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.getRoles().add(managerRole);
        return userRepository.save(user);
    }

    public Restaurant saveRestaurant(RestaurantUpdateDTO restaurantDTO) {
        Restaurant newRestaurant = new Restaurant();
        Address newAddress = saveAddress(restaurantDTO.getAddress());
        newRestaurant.setManager(userRepository.findById(restaurantDTO.getManagerId()).orElse(null));
        newRestaurant.setRestaurantName(restaurantDTO.getRestaurantName());
        newRestaurant.setDescription(restaurantDTO.getDescription());
        newRestaurant.setOpensAt(restaurantDTO.getOpensAt());
        newRestaurant.setClosesAt(restaurantDTO.getClosesAt());
        newRestaurant.setPrice(restaurantDTO.getPrice());
        newRestaurant.setWidth(650);
        newRestaurant.setHeight(480);
        newRestaurant.setAddedDate(new Date());
        Restaurant rest = restaurantRepository.save(newRestaurant);

        rest.setKitchenTypes(restaurantDTO.getKitchenTypes());
        rest.setLocalTypes(restaurantDTO.getLocalTypes());
        rest.setAddress(newAddress);
        restaurantRepository.save(rest);
        restaurantDTO.setId(rest.getId());
        updateTags(restaurantDTO, rest);
        return rest;
    }

    public Restaurant updateRestaurant(RestaurantUpdateDTO restaurantDTO) {
        Restaurant updatedRestaurant = new Restaurant();

        updatedRestaurant.setId(restaurantDTO.getId());
        updatedRestaurant.setRestaurantName(restaurantDTO.getRestaurantName());
        updatedRestaurant.setManager(userRepository.findById(restaurantDTO.getManagerId()).orElse(null));
        updatedRestaurant.setDescription(restaurantDTO.getDescription());
        updatedRestaurant.setOpensAt(restaurantDTO.getOpensAt());
        updatedRestaurant.setClosesAt(restaurantDTO.getClosesAt());
        updatedRestaurant.setPrice(restaurantDTO.getPrice());
        updatedRestaurant.setKitchenTypes(restaurantDTO.getKitchenTypes());
        updatedRestaurant.setLocalTypes(restaurantDTO.getLocalTypes());
        updatedRestaurant.setAddress(restaurantDTO.getAddress());

        saveAddress(restaurantDTO.getAddress());

        Restaurant rest = restaurantRepository.save(updatedRestaurant);
        updateTags(restaurantDTO, rest);
        return rest;
    }

    public List<Images> findImagesByRestaurantId(String restaurantId) {
        Optional<List<Images>> opt = dbFileRepository.findAllByRestaurantId(Integer.parseInt(restaurantId));
        return opt.orElse(null);
    }

    public long checkFilesNumberSmallerThanTen(String restaurantId) {
        return dbFileRepository.countAllByRestaurant_Id(Integer.parseInt(restaurantId));
    }

    public Images storeFile(MultipartFile file, String restaurantId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Images dbFile = new Images(fileName, file.getContentType(), file.getBytes(),
                    restaurantRepository.findById(Integer.parseInt(restaurantId)).orElse(null));

            return dbFileRepository.save(dbFile);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Images getFile(String fileId) {
        return dbFileRepository.findById(Long.valueOf(fileId))
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
    }

    public void checkIfManagerHasRestaurant(User manager) throws RuntimeException {
        if (restaurantRepository.findAllByManagerId(manager.getId()).isPresent())
            throw new RuntimeException("Managerul cu acest email detine deja un restaurant.");
    }

    @Transactional
    public void deleteFile(String fileId) {
        dbFileRepository.deleteById(Long.valueOf(fileId));
    }

    public ResponseEntity<?> checkIfManager(String managerEmail) {
        try {
            User user = service.findUserByUsername(managerEmail);
            checkIfManagerHasRestaurant(user);
            return ResponseEntity.ok(user);
        } catch (RuntimeException exception) {
            return ResponseEntity.badRequest().body(new MessageResponse(exception.getMessage()));
        }
    }
}
