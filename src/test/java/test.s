	.text
	.globl	main
main:
	addi sp, sp, -12
	sw ra, 12-4(sp)
	sw fp, 12-8(sp)
	addi fp, sp, 12
	li t1,1
	addi sp,sp,4
	sw t1, 0(sp)
	li t1,2
	addi sp,sp,4
	sw t1, 0(sp)
	lw t2, 0(sp)
	addi sp,sp,-4
	lw t1, 0(sp)
	addi sp,sp,-4
	add t1,t1,t2
	addi sp,sp,4
	sw t1, 0(sp)
	j main_epilogue
main_epilogue:	lw a0, 0(sp)
	addi sp, sp, 4
	lw fp, 12-8(sp)
	lw ra, 12-4(sp)
	addi sp, sp, 12
jr ra