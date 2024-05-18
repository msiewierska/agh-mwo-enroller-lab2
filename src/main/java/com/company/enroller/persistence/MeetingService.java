package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

	Session session;

	public MeetingService() {
		session = DatabaseConnector.getInstance().getSession();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query query = this.session.createQuery(hql);
		return query.list();
	}

	public Collection<Meeting> getById(long id) {
		String hql = "FROM Meeting WHERE id LIKE :id";
		Query query = this.session.createQuery(hql);
		query.setParameter("id", id);
		return query.list();
	}

	public Meeting add(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.save(meeting);
		transaction.commit();
		return meeting;
	}

	public void update(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.merge(meeting);
		transaction.commit();
	}

	public void delete(Meeting meeting) {
		Transaction transaction = session.beginTransaction();
		session.delete(meeting);
		transaction.commit();
	}

	public Meeting findById(long id) {
		return session.get(Meeting.class, id);
	}


}
