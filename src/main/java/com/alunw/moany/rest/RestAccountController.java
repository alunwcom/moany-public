package com.alunw.moany.rest;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alunw.moany.model.Account;
import com.alunw.moany.model.AccountType;
import com.alunw.moany.repository.AccountRepository;
import com.alunw.moany.services.AccountService;
import com.alunw.moany.services.TransactionService;
import com.alunw.moany.services.validators.AccountValidator;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "Account API", description= "Access accounts")
@RequestMapping("/rest/accounts/v2/")
public class RestAccountController {
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	AccountValidator accountValidator;
	
	@Autowired
	private TransactionService transactionService;
	
	private static Logger logger = LoggerFactory.getLogger(RestAccountController.class);
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.setValidator(accountValidator);
	}
	
	/**
	 * GET a list of all accounts.
	 * 
	 * @return A list of all accounts.
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	public List<Account> getAccounts(Principal principal, HttpServletRequest request) {
		logger.info("getAccounts() [user={}, remote={}]", principal.getName(), request.getRemoteAddr());
		return accountRepo.findAll();
	}
	
	/**
	 * GET a single account by account id.
	 * 
	 * @param accountId An account id (UUID).
	 * @return An account.
	 */
	@RequestMapping(value={"/id","/id/{accountId}"}, method = RequestMethod.GET)
	public ResponseEntity<Account> getAccountById(@PathVariable(name="accountId", required=false) String accountId, 
			Principal principal, HttpServletRequest request) {
		
		logger.info("getAccountById({}) [user={}, remote={}]", accountId, principal.getName(), request.getRemoteAddr());
		
		if (accountId == null) {
			throw new BadRequestException();
		}
		
		Optional<Account> result = accountRepo.findById(accountId);
		if (result.isPresent()) {
			return ResponseEntity.ok(result.get());
		} else {
			throw new ResourceNotFoundException();
		}
	}
	
	/**
	 * DELETE a single account by account id, and return that account. (No action if the account id does not exist, and
	 * nothing is returned.)
	 * 
	 * @param accountId An account id (UUID).
	 * @return An account.
	 */
	@RequestMapping(value={"/id","/id/{accountId}"}, method = RequestMethod.DELETE)
	public void deleteAccountById(@PathVariable(name="accountId", required=false) String accountId, 
			Principal principal, HttpServletRequest request) {
		
		logger.info("deleteAccountById({}) [user={}, remote={}]", accountId, principal.getName(), request.getRemoteAddr());
		
		if (accountId == null) {
			throw new BadRequestException();
		}
		
		accountRepo.deleteById(accountId);
	}
	
	/**
	 * PUT an account record in the repository, returning the account. If the id is blank, then a new account record is
	 * created, and the id is included in the returned account. If the id is specified, then this account will be 
	 * updated and the account returned. If the specified id does not exist, then the record is not stored, and an
	 * error response is returned.
	 * 
	 * If there is a validation error (e.g. no account number or name), then the record is not stored, and an error 
	 * response returned.
	 * 
	 * @param account An account
	 * @return An account.
	 */
	@RequestMapping(value={"/"}, method = RequestMethod.PUT)
	public Account putAccountById(@Validated @RequestBody Account account, BindingResult result, Model model) {
		logger.info("putAccountById()");
		// TODO testing
		if (result != null) {
			logger.info("errors = " + result.getErrorCount());
			logger.info("errors = " + result.getAllErrors());
		}
		
		// TODO Validate data
		if (account != null) {
			logger.debug("account = " + account);
		}
		// Store data
		Account acc = accountRepo.save(account);
		
		// Re-calculate balances
		transactionService.recalculateBalances(acc);
		
		return acc;
	}
	
	@RequestMapping(value={"/balance/"}, method = RequestMethod.GET)
	@Transactional
	public BigDecimal broughtForwardbalance(
		@RequestParam(name="acc", required = false) String accountIds,
		@RequestParam(name="date", required = false) String date) {
		
		logger.info("broughtForwardbalance({}, {})", accountIds, date);
		
		return accountService.findBroughtForwardBalance(accountIds, date);
	}
	
	@RequestMapping(value={"/rebalance/{transactionId}"}, method = RequestMethod.GET)
	@Transactional
	public ResponseEntity<Object> rebalanceAccountFromTransaction(@PathVariable String transactionId) {
		
		logger.info("rebalanceAccountFromTransaction({})", transactionId);
		
		// validate: transaction exists and has a balance
		if (transactionId == null || transactionId.isEmpty()) {
			logger.warn("No transaction ID supplied - bad request!");
			throw new BadRequestException();
		}
		
		transactionService.rebalanceAccountFromTransaction(transactionId);
		
		// TODO success body??
		return ResponseEntity.ok("{}");
	}
	
	@RequestMapping(value={"/types"}, method = RequestMethod.GET)
	public List<AccountType> getAccountTypes() {
		
		logger.info("getAccountTypes()");
		
		return Arrays.asList(AccountType.values());
	}
}
