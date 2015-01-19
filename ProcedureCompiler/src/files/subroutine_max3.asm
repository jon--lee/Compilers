	.text
	.globl main
	main: #QTSPIM will automatically look for main
	subu $sp $sp 4
	sw $ra ($sp)		#save the ra

	li $v0 10
	subu $sp $sp 4
	sw $v0 ($sp)		#load first argument to stack

	li $v0 15
	subu $sp $sp 4
	sw $v0 ($sp)		#load second argument to stack

	li $v0 20
	subu $sp $sp 4
	sw $v0 ($sp)		#load third argument to stack

	jal procmax3		#execute

	lw $v0 ($sp)		#load return value from top of stack

	addu $sp $sp 4
	lw $t0 ($sp)
	addu $sp $sp 4
	lw $t0 ($sp)		#eliminate arguments from stack

	addu $sp $sp 4
	lw $ra ($sp)		#get the return address back

	move $a0 $v0
	li $v0 1
	syscall				#print out the return value

	li $v0 10
	syscall # halt

procmax3:
	li $v0 0
	subu $sp $sp 4
	sw $v0 ($sp)		#return value space allocated


	#load first argument on stack to t1
	addu $t0 $sp 12
	lw $t1 ($t0)

	#load second argument on stack to t2
	addu $t0 $sp 8
	lw $t2 ($t0)

	#push ra to stack
	subu $sp $sp 4
	sw $ra ($sp)

	#push t1 to stakc, push t2 to stack
	subu $sp $sp 4
	sw $t1 ($sp)

	subu $sp $sp 4
	sw $t2 ($sp)

	#execute
	jal procmax2

	#pop return value
	lw $v0 ($sp)
	addu $sp $sp 4

	#pop arguments
	lw $t0 ($sp)
	addu $sp $sp 4

	lw $t0 ($sp)
	addu $sp $sp 4

	#pop return address
	lw $ra ($sp)
	addu $sp $sp 4

	#load return value to t1
	move $t1 $v0

	#load third argument to t2
	addu $t0 $sp 4
	lw $t2 ($t0)

	#push ra to stack, push t1 to stack, push t2 to stack
	subu $sp $sp 4
	sw $ra ($sp)

	subu $sp $sp 4
	sw $t1 ($sp)

	subu $sp $sp 4
	sw $t2 ($sp)

	#execute
	jal procmax2

	#pop return value
	lw $v0 ($sp)
	addu $sp $sp 4
	#pop arguments
	lw $t0 ($sp)
	addu $sp $sp 4

	lw $t0 ($sp)
	addu $sp $sp 4

	#pop return address
	lw $ra ($sp)
	addu $sp $sp 4

	#set top of stack (ie at sp) to return value
	sw $v0 ($sp)

	#return to current ra
	jr $ra


procmax2:
	li $v0 0
	subu $sp $sp 4
	sw $v0 ($sp)
	#top of the stack is now return variable

	addu $t0 $sp 4 #first value on stack (not return) which is second argument
	lw $v0 ($t0)
	sw $v0 ($sp)	#save second parameter to return value (first on stack ie no height)

	addu $t0 $sp 8
	lw $a0 ($t0)		#load first parameter to a0
	addu $t0 $sp 4
	lw $v0 ($t0)		#load second parameter to v0
	ble $a0 $v0 endif1   #skip to endif1 if not true
	sw $a0 ($sp)
endif1:
	jr $ra
	.data
varignore:
	.word 0
newline:
	.asciiz "\n"
