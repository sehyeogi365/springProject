package com.spring.board.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spring.board.HomeController;
import com.spring.board.service.boardService;
import com.spring.board.vo.BoardVo;
import com.spring.board.vo.CodeVo;
import com.spring.board.vo.PageVo;
import com.spring.board.vo.UserVo;
import com.spring.common.CommonUtil;

@Controller
public class BoardController {
	
	@Autowired 
	boardService boardService;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	

	//boardWriteAction참고해보기 @ResponseBody 추가하기 
	@RequestMapping(value = "/board/boardList.do", method = RequestMethod.GET)
	public String boardList(Locale locale, Model model,PageVo pageVo,CodeVo codeVo
			, @RequestParam(value = "boardTypeList", required = false)ArrayList<String> boardTypeList) throws Exception{
		
		//������ �Ķ���� ����� List<String> boardTypeList �̷�������
		//List<BoardVo> boardList = new ArrayList<BoardVo>();
		List<BoardVo> boardList = null; //�̷��� ����
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		CommonUtil commonUtil = new CommonUtil();
		int page = 1;
		int totalCnt = 0;
		List<CodeVo> codeNameList = boardService.selectCodeNamePhone(codeVo);

		if(pageVo.getPageNo() == 0){
			pageVo.setPageNo(page);
		}
		
		//�˻� ���� ������ ������ service �޼ҵ嵵 �ΰ� �ľ��� �ᱹ�� boardList ������ ���� ���� ����
		if(boardTypeList != null && !boardTypeList.isEmpty()) {
			boardList = boardService.SelectBoardListByBoardType(pageVo, boardTypeList);
			System.out.println("Valid Board Types: " + boardTypeList);
			
		} else {
			boardList = boardService.SelectBoardList(pageVo);
		}

		totalCnt = boardService.selectBoardCnt(boardTypeList);

		model.addAttribute("boardList", boardList);
		model.addAttribute("totalCnt", totalCnt);
		model.addAttribute("pageNo", page);
		model.addAttribute("codeNameList", codeNameList);
		
		result.put("success", (boardList != null)?"Y":"N");
		result.put("boardList", boardList);
		result.put("totalCnt", totalCnt);
		result.put("pageNo", page);
		result.put("codeNameList", codeNameList);

		return "/board/boardList";
		//return result;		
	}
	
	@RequestMapping(value = "/board/boardAjaxList.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> boardCheckboxList(Locale locale, Model model,PageVo pageVo,CodeVo codeVo
			, @RequestParam(value = "boardTypeList", required = false)ArrayList<String> boardTypeList
			) throws Exception{
		
		List<BoardVo> boardList = null; 
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		CommonUtil commonUtil = new CommonUtil();
		int page = 1;
		int totalCnt = 0;
		List<CodeVo> codeNameList = boardService.selectCodeNamePhone(codeVo);

		if(pageVo.getPageNo() == 0){
			pageVo.setPageNo(page);
		}
		
		if(boardTypeList != null && !boardTypeList.isEmpty()) {
			boardList = boardService.SelectBoardListByBoardType(pageVo, boardTypeList);
			System.out.println("Valid Board Types: " + boardTypeList);
			
		} else {
			boardList = boardService.SelectBoardList(pageVo);
		}

		totalCnt = boardService.selectBoardCnt(boardTypeList);

		model.addAttribute("boardList", boardList);
		model.addAttribute("totalCnt", totalCnt);
		model.addAttribute("pageNo", page);
		model.addAttribute("codeNameList", codeNameList);
		
		result.put("success", (boardList != null)?"Y":"N");
		result.put("boardList", boardList);
		result.put("totalCnt", totalCnt);
		result.put("pageNo", page);
		result.put("codeNameList", codeNameList);
		//System.out.println("result" + result);
		return result;
	}
	
	@RequestMapping(value = "/board/{boardType}/{boardNum}/boardView.do", method = RequestMethod.GET)
	public String boardView(Locale locale, Model model
			,@PathVariable("boardType")String boardType
			,@PathVariable("boardNum")int boardNum) throws Exception{
		
		BoardVo boardVo = new BoardVo();//BoardVo�� �� �� ������Ƽ���� �ִٸ� ���� PathVartiable������ص� �ȴ�
		
		
		boardVo = boardService.selectBoard(boardType,boardNum);
		
		model.addAttribute("boardType", boardType);
		model.addAttribute("boardNum", boardNum);
		model.addAttribute("board", boardVo);
		
		System.out.println("creator명" + boardVo.getCreator());
		return "board/boardView";
	}
	
	@RequestMapping(value="/board/getCodeNames.do", method = RequestMethod.GET)
	public List<CodeVo> getCodeNames(CodeVo codeVo) throws Exception{
		
		List<CodeVo> codeNameList = boardService.selectCodeNamePhone(codeVo);
		
		return codeNameList;
	
	}
	
	@RequestMapping(value = "/board/boardWrite.do", method = RequestMethod.GET)
	public String boardWrite(Locale locale, Model model, CodeVo codeVo) throws Exception{
		
		List<CodeVo> codeNameList = boardService.selectCodeNamePhone(codeVo);
		
		model.addAttribute("codeNameList", codeNameList);
		
		return "board/boardWrite";
	}
	
	@RequestMapping(value = "/board/boardWriteAction.do", method = RequestMethod.POST)
	@ResponseBody
	public String boardWriteAction(Locale locale,BoardVo boardVo) throws Exception{
		
		HashMap<String, String> result = new HashMap<String, String>();
		CommonUtil commonUtil = new CommonUtil();
		
		int resultCnt = boardService.boardInsert(boardVo);
		
		result.put("success", (resultCnt > 0)?"Y":"N");
		
		String callbackMsg = commonUtil.getJsonCallBackString(" ",result);
		
		System.out.println("callbackMsg::"+callbackMsg);
		
		return callbackMsg;
	}
	
	//수정 페이지
	@RequestMapping(value = "/board/{boardType}/{boardNum}/boardModify.do", method = RequestMethod.GET)
	public String boardModify(Locale locale, Model model
			,@PathVariable("boardType")String boardType
			,@PathVariable("boardNum")int boardNum) throws Exception{
		
		BoardVo boardVo = new BoardVo();//BoardVo�� �� �� ������Ƽ���� �ִٸ� ���� PathVartiable������ص� �ȴ�
		
		
		boardVo = boardService.selectBoard(boardType,boardNum);
	
		model.addAttribute("boardType", boardType);
		model.addAttribute("boardNum", boardNum);
		model.addAttribute("board", boardVo);

		return "board/boardModify";
	}
	//수정
	@RequestMapping(value = "/board/boardModifyAction.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> boardModifyAction(Locale locale, BoardVo boardVo) throws Exception {
		
		Map<String, String> result = new HashMap<String, String>();
		
		CommonUtil commonUtil = new CommonUtil();
		
		int resultCnt = boardService.boardModify(boardVo);
		
		result.put("success", (resultCnt > 0)?"Y":"N");
	
		//String callbackMsg = commonUtil.getJsonCallBackString(" ", result);
		
		return result;	
	}

	//삭제
	@RequestMapping(value = "/board/boardDeleteAction.do", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> boardDeleteAction(Locale locale, BoardVo boardVo) throws Exception{
		
		Map<String, String> result = new HashMap<String, String>();
		
		CommonUtil commonUtil = new CommonUtil();
		
		int resultCnt = boardService.boardDelete(boardVo);
		
		result.put("success", (resultCnt > 0)?"Y":"N");
		
		return result;		
	}

	@RequestMapping(value = "/board/joinPage.do", method = RequestMethod.GET)
	public String joinPage(Locale locale, Model model, CodeVo codeVo) throws Exception{
		
		List<CodeVo> codePhoneList = boardService.selectCodeNamePhone(codeVo);
		
		model.addAttribute("codePhoneList", codePhoneList);
		
		return "/board/join";
	}
	
	//회원가입
	@RequestMapping(value = "/board/joinAction.do", method = RequestMethod.POST
			, produces="application/text;charset=utf-8")
	@ResponseBody
	public String joinAction(Locale locale, UserVo userVo) throws Exception{
		
		HashMap<String, String> result = new HashMap<String, String>();
		CommonUtil commonUtil = new CommonUtil();
		
		int resultCnt = boardService.insertUser(userVo);
		
		result.put("success", (resultCnt > 0)?"Y":"N");
		
		String callbackMsg = commonUtil.getJsonCallBackString(" ",result);
		
		System.out.println("callbackMsg::"+callbackMsg);
		
		return callbackMsg;
	}

	//중복아이디 검사
	@RequestMapping(value ="/board/isduplicateId.do", method = RequestMethod.GET)
	@ResponseBody
	public String isDuplicateId(Locale locale, UserVo uservo) throws Exception{
		
		HashMap<String, String> result = new HashMap<String, String>();
		CommonUtil commonUtil = new CommonUtil();
		
		boolean isDuplicated = boardService.isDuplicateId(uservo);
		
		result.put("success", (isDuplicated)?"Y":"N");
		
		String callbackMsg = commonUtil.getJsonCallBackString(" ",result);
		
		System.out.println("callbackMsg::"+callbackMsg);
		
		
		return callbackMsg;
	}
	
	@RequestMapping(value = "/board/loginPage.do", method = RequestMethod.GET)
	public String loginPage() throws Exception{
		
		return "/board/login";
	}
	
	@RequestMapping(value = "/board/loginAction.do", method = RequestMethod.POST)
	@ResponseBody
	public String loginAction(Locale locale, UserVo uservo, HttpSession session) throws Exception{
		
		HashMap<String, String> result = new HashMap<String, String>();
		CommonUtil commonUtil = new CommonUtil();

		UserVo getUser = boardService.selectUser(uservo);
		
		result.put("success", (getUser != null)?"Y":"N");
		
		if(getUser!=null) {
			
			session.setAttribute("userId", getUser.getUserId());
			session.setAttribute("userName", getUser.getUserName());
		}
		
		String callbackMsg = commonUtil.getJsonCallBackString(" ",result);
		
		System.out.println("callbackMsg::"+callbackMsg);
		
		return callbackMsg;
	}
	
	@RequestMapping(value = "/board/logoutAction.do", method = RequestMethod.GET)
	public String logoutAction(HttpServletRequest request){
		
		HttpSession session = request.getSession();
		session.removeAttribute("userId");
		session.removeAttribute("userName");
		
		return "redirect:/board/boardList.do";
	}


	
	
}
