	.text
	.globl	main
main:
	addi sp, sp, -8
	sw ra, 4(sp)
	sw fp, 0(sp)
	addi fp, sp, 8
	li t1,100
	addi sp,sp,4
	sw t1, 0(sp)
	j main_epilogue
main_epilogue:
	lw a0, 0(sp)
	addi sp, sp, 4
	lw fp, 0(sp)
	lw ra, 4(sp)
	addi sp, sp, 8
	jr ra
