package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Part;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meetings")
public class MeetingRestController {

	@Autowired
	MeetingService meetingService;

	@Autowired
	ParticipantService participantsService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {

		Collection<Meeting> meetings = meetingService.getAll();

		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("id") long id) {
		Collection<Meeting> meeting = meetingService.getById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Meeting>>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") long id) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.delete(meeting);
		return new ResponseEntity<Meeting>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody Meeting updatedMeeting) {
		Meeting meeting = meetingService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.update(meeting);
		return new ResponseEntity<Meeting>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("id") long id) {
		Collection<Meeting> meeting = meetingService.getById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		Collection<Participant> participants = meeting.iterator().hasNext() ? meeting.iterator().next().getParticipants() : null;
		if (participants == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	public ResponseEntity<?> addMeetingParticipant(@PathVariable("id") long id, @RequestBody String participantToAddLogin) {
		Collection<Meeting> meeting = meetingService.getById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		Collection<Participant> participants = meeting.iterator().next().getParticipants();

		if (participants.stream().anyMatch((par) -> par.getLogin().equals(participantToAddLogin))) {
			return new ResponseEntity<String>(
					"Unable to add. A participant with login " + participantToAddLogin + " already is enrolled.",
					HttpStatus.CONFLICT);
		}

		Participant participantToAdd = participantsService.findByLogin(participantToAddLogin);

		if (participantToAdd == null) {
			return new ResponseEntity<String>(
					"Unable to add. A participant with login " + participantToAddLogin + " does not exists in the system.",
					HttpStatus.CONFLICT);
		}

		meeting.iterator().next().addParticipant(participantToAdd);
		return new ResponseEntity<Meeting>(meeting.iterator().next(), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}/participants/{login}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable("id") long id, @PathVariable("login") String login) {
		Collection<Meeting> meeting = meetingService.getById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}

		Collection<Participant> participants = meeting.iterator().next().getParticipants();

		Participant participantToDelete = participants.stream().filter(participant -> participant.getLogin().equals(login)).iterator().next();

		if (participantToDelete == null) {
			return new ResponseEntity<String>(
					"Unable to delete. A participant with login " + login + " is not enrolled.",
					HttpStatus.CONFLICT);
		}
		meeting.iterator().next().removeParticipant(participantToDelete);
		return new ResponseEntity<Meeting>(meeting.iterator().next(), HttpStatus.OK);
	}
}
