package it.unical.asde2018.sudoku.components.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.unical.asde2018.sudoku.components.persistence.UserDAO;
import it.unical.asde2018.sudoku.components.services.LobbyService;
import it.unical.asde2018.sudoku.logic.util.Difficulty;
import it.unical.asde2018.sudoku.model.User;

@Controller
public class LobbyController {

	@Autowired
	private LobbyService lobbyService;

	@Autowired
	private UserDAO userDao;

	@PostMapping("create-lobby")
	public String createRoom(@RequestParam String lobbyName, @RequestParam String difficulty, HttpSession session) {

		if (session.getAttribute("sudoku") == null) {
			String username = (String) session.getAttribute("username");
			User user = userDao.getUser(username);
			int id_room = lobbyService.createRoom(user, Difficulty.valueOf(difficulty), lobbyName);
			// if(session.getAttribute("sudoku") == null)
			session.setAttribute("sudoku", lobbyService.getMatches().get(id_room).getSudokuToSolve());
			session.setAttribute("idRoom", id_room);
		}
		return "sudoku_game_board";
	}

	@PostMapping("joinRoom")
	public String joinRoom(@RequestParam String room, HttpSession session) {

		if (session.getAttribute("sudoku") == null) {
			String username = (String) session.getAttribute("username");
			User user = userDao.getUser(username);
			lobbyService.joinRoom(user, Integer.parseInt(room));

			session.setAttribute("sudoku", lobbyService.getMatches().get(Integer.parseInt(room)).getSudokuToSolve());
			session.setAttribute("idRoom", Integer.parseInt(room));
		}
		return "sudoku_game_board";

	}

	@PostMapping("roomPagination")
	public void switchRoomPagination(@RequestParam String index, HttpSession session, HttpServletResponse response) {
		int requested_pagination = Integer.parseInt(index);
		int finalIndex = requested_pagination * LobbyService.getMatchesToShow();

		session.setAttribute("currentPagination", requested_pagination);
		session.setAttribute("total_room_page", lobbyService.getTotalRoomPage());
		session.setAttribute("available_room", lobbyService.getRoomInTheWindow(finalIndex));

		response.setStatus(HttpServletResponse.SC_ACCEPTED);
	}

	@PostMapping("leaveRoom")
	public String leaveRoom(@RequestParam String idRoom, HttpSession session) {

		lobbyService.getMatches().remove(Integer.parseInt(idRoom));
		session.removeAttribute("sudoku");
		session.removeAttribute("idRoom");

		return "redirect:/";
	}

}
